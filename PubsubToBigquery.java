package com.dxc.pipeline;

import com.dxc.model.PubSubJsonModel;
import com.google.api.services.bigquery.model.TableFieldSchema;
import com.google.api.services.bigquery.model.TableReference;
import com.google.api.services.bigquery.model.TableRow;
import com.google.api.services.bigquery.model.TableSchema;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.beam.sdk.Pipeline;
import org.apache.beam.sdk.io.gcp.bigquery.BigQueryIO;
import org.apache.beam.sdk.io.gcp.bigquery.BigQueryIO.Write.WriteDisposition;
import org.apache.beam.sdk.io.gcp.pubsub.PubsubIO;
import org.apache.beam.sdk.options.PipelineOptions;
import org.apache.beam.sdk.options.PipelineOptionsFactory;
import org.apache.beam.sdk.transforms.DoFn;
import org.apache.beam.sdk.transforms.ParDo;
import org.apache.beam.sdk.values.PCollection;

import java.util.ArrayList;
import java.util.List;

public class PubsubToBigquery {

    public static class FormatBigquery extends DoFn<String, TableRow> {
        @ProcessElement
        public void processElement(ProcessContext c) {
            Gson gson = new GsonBuilder().create();

            PubSubJsonModel parseMap = gson.fromJson(c.element().toString(), PubSubJsonModel.class);
            TableRow row = new TableRow();
            TableRow name = row.set("Name", parseMap.getName());
            TableRow age = row.set("Age",parseMap.getAge());
        }

        /*logic*/
        static TableSchema getSchema() {
            List<TableFieldSchema> fields = new ArrayList<>();

            fields.add(new TableFieldSchema().setName("Name").setType("STRING").setMode("REQUIRED"));
            fields.add(new TableFieldSchema().setName("Age").setType("INTEGER").setMode("REQUIRED"));

            return new TableSchema().setFields(fields);
        }
    }

    public static void main(String[] args) throws Throwable {
        String PubsubTopic = "projects/future-silicon-342405/topics/alert-policy-trigger";
        String tempLocationPath = "gs://dataflow_ex/staging";

        TableReference tableRef = new TableReference();
        tableRef.setProjectId("future-silicon-342405");
        tableRef.setDatasetId("Employee");
        tableRef.setTableId("Details");
        PipelineOptions options = PipelineOptionsFactory.fromArgs(args).withValidation().create();


        options.setTempLocation(tempLocationPath);
        options.setJobName("TexttoBq");


        Pipeline pipeline = Pipeline.create(options);
        PCollection<String> message = pipeline.apply("Read pubsub message", PubsubIO.readStrings().fromTopic(PubsubTopic));

        PCollection<TableRow> bqData = message.apply("Convert to BigQuery TableRow", ParDo.of(new FormatBigquery()));
        bqData.apply("Write into BigQuery",
                BigQueryIO.writeTableRows().to(tableRef).withSchema(FormatBigquery.getSchema())
                        .withCreateDisposition(BigQueryIO.Write.CreateDisposition.CREATE_IF_NEEDED)
                        .withWriteDisposition(WriteDisposition.WRITE_APPEND));
        pipeline.run().waitUntilFinish();
    }
}

