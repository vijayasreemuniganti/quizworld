package com.dxc.pipeline;

import java.util.ArrayList;
import java.util.List;

import org.apache.beam.sdk.io.gcp.bigquery.BigQueryIO.Write.WriteDisposition;
import org.apache.beam.sdk.Pipeline;
import org.apache.beam.sdk.io.TextIO;
import org.apache.beam.sdk.io.gcp.bigquery.BigQueryIO;
import org.apache.beam.sdk.options.PipelineOptions;
import org.apache.beam.sdk.options.PipelineOptionsFactory;
//import org.apache.beam.sdk.transforms.Create;
import org.apache.beam.sdk.transforms.DoFn;
//import org.apache.beam.sdk.transforms.MapElements;
import org.apache.beam.sdk.transforms.ParDo;
import com.google.api.services.bigquery.model.TableReference;
import com.google.api.services.bigquery.model.TableRow;
import com.google.api.services.bigquery.model.TableSchema;
import com.google.api.services.bigquery.model.TableFieldSchema;
import org.apache.beam.sdk.values.PCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CsvToBQPipeline {
    private static final Logger LOG = LoggerFactory.getLogger(CsvToBQPipeline.class);

    private static String column = "City,Temp,Month,Date";
    public static class FormatForBigquery extends DoFn<String, TableRow> {

        private String[] columnNames = column.split(",");
        @ProcessElement
        public void processElement(ProcessContext c) {
            TableRow row = new TableRow();
            String[] parts = c.element().split(",");
            if (!c.element().contains(column)) {
                for (int i = 0; i < parts.length; i++) {

                    row.set(columnNames[i], parts[i]);
                }
                c.output(row);
            }
        }
        /** Defines the BigQuery schema  */
        static TableSchema getSchema() {
            List<TableFieldSchema> fields = new ArrayList<>();

            fields.add(new TableFieldSchema().setName("City").setType("STRING").setMode("REQUIRED"));
            fields.add(new TableFieldSchema().setName("Temp").setType("INTEGER").setMode("REQUIRED"));
            fields.add(new TableFieldSchema().setName("Month").setType("STRING"));
            fields.add(new TableFieldSchema().setName("Date").setType("DATE"));
            return new TableSchema().setFields(fields);
        }
    }
    public static void main(String[] args) throws Throwable {

        String sourceFilePath = "gs://dataflow_ex/sample.csv";
        String tempLocationPath = "gs://dataflow_ex/staging";

        TableReference tableRef = new TableReference();
        tableRef.setProjectId("future-silicon-342405");
        tableRef.setDatasetId("sample_ds");
        tableRef.setTableId("sample");
        PipelineOptions options = PipelineOptionsFactory.fromArgs(args).withValidation().create();

        options.setTempLocation(tempLocationPath);
        options.setJobName("TexttoBq");

        Pipeline pipeline = Pipeline.create(options);
        PCollection<String> csdata = pipeline.apply("Read CSV File", TextIO.read().from(sourceFilePath));

        PCollection<TableRow> bqData = csdata.apply("Convert to BigQuery TableRow", ParDo.of(new FormatForBigquery()));
        bqData.apply("Write into BigQuery",
                        BigQueryIO.writeTableRows().to(tableRef).withSchema(FormatForBigquery.getSchema())
                                .withCreateDisposition(BigQueryIO.Write.CreateDisposition.CREATE_IF_NEEDED)
                                .withWriteDisposition(WriteDisposition.WRITE_APPEND));
        pipeline.run().waitUntilFinish();
    }
}

