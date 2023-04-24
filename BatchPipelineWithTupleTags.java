package com.dxc.pipeline;

import com.google.api.services.bigquery.model.TableReference;
import com.google.api.services.bigquery.model.TableRow;
import org.apache.beam.runners.dataflow.options.DataflowPipelineOptions;
import org.apache.beam.runners.direct.DirectRunner;
import org.apache.beam.sdk.Pipeline;
import org.apache.beam.sdk.coders.KvCoder;
import org.apache.beam.sdk.coders.NullableCoder;
import org.apache.beam.sdk.coders.StringUtf8Coder;
import org.apache.beam.sdk.io.FileIO;
import org.apache.beam.sdk.io.TextIO;
import org.apache.beam.sdk.io.gcp.bigquery.BigQueryIO;
import org.apache.beam.sdk.io.gcp.bigquery.TableRowJsonCoder;
import org.apache.beam.sdk.options.PipelineOptionsFactory;
import org.apache.beam.sdk.transforms.DoFn;
import org.apache.beam.sdk.transforms.ParDo;
import org.apache.beam.sdk.values.*;
import org.joda.time.DateTime;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.logging.FileHandler;


      /* Specify the file format while reading from GCS bucket.
         4. Read about parameters of withOutputTags
         6. Write failed table rows to Bq in another table
         7. Write failed files from GCs to another bucket
         8. Write success files to archieve bucket
         and delete from original location files. */





public class BatchPipelineWithTupleTags {

    public static void main(String[] args) {
        String archivebuck = "gs;//bucket_archive/files";
        String OutputPath = "gs://dataflow_ex/staging";
        String ErrorTable = "future-silicon-342405:sample_ds:sample";

        /*
         * Initialize Pipeline Configurations
         */
        DataflowPipelineOptions options = PipelineOptionsFactory
                .as(DataflowPipelineOptions.class);
        options.setRunner(DirectRunner.class);
        options.setProject("");
        options.setStreaming(true);
        options.setTempLocation("");
        options.setStagingLocation("");
        options.setRegion("");
        options.setMaxNumWorkers(1);
        options.setWorkerMachineType("n1-standard-1");

        Pipeline pipeline = Pipeline.create(options);

        /*
         * Read files from GCS buckets
         * dump_21_06_2022.csv
         * dump_20_06_2022.csv
         * dump_19_06_2022.csv
         * .
         * .
         * .
         * .
         * .
         * dump_03_03_2022.pdf  // Pipeline will break here
         * dump_01_01_2022.csv
         */
        PCollection<FileIO.ReadableFile> data = pipeline.apply(FileIO.match().filepattern("gs://dump_*.csv")).apply(FileIO.readMatches());
        // .apply(FileIO.readMatches().withCompression(GZIP))
        /*
         * Create Tuple Tag to process passed as well as failed records while parsing in ParDo functions
         */
        final TupleTag<KV<String, String>> mapSuccessTag = new TupleTag<KV<String, String>>() {
            private static final long serialVersionUID = 1L;
        };
        final TupleTag<KV<String, String>> mapFailedTag = new TupleTag<KV<String, String>>() {
            private static final long serialVersionUID = 1L;
        };

        //final TupleTag<String> failedFiles = new TupleTag<String>();
        //final TupleTag<String> successFiles = new TupleTag<String>();


        PCollectionTuple mapTupleObj = data.apply(ParDo.of(new MapTransformation(mapSuccessTag, mapFailedTag))
                .withOutputTags(mapSuccessTag, TupleTagList.of(mapFailedTag)));

        PCollection<KV<String, String>> map = mapTupleObj.get(mapSuccessTag).setCoder(KvCoder.of(StringUtf8Coder.of(), StringUtf8Coder.of()));
// push failed files

        //PCollection<String> Failed = mapTupleObj.get(mapFailedTag.toString());
       // Failed.apply(TextIO.write().to(OutputPath));
// push success files to archive

        //PCollection<String> SuccessToGcs = mapTupleObj.get(mapSuccessTag.toString());
        //SuccessToGcs.apply(TextIO.write().to(archivebuck));

        /*
         * Create Tuple Tag to process passed as well as failed records while parsing in ParDo functions.
         * Failed Tags can be pushed in failed table.
         */
        final TupleTag<TableRow> tableRowSuccessTag = new TupleTag<TableRow>() {
            private static final long serialVersionUID = 1L;
        };
        final TupleTag<TableRow> tableRowFailedTag = new TupleTag<TableRow>() {
            private static final long serialVersionUID = 1L;
        };

        PCollectionTuple tableRowTupleObj = map.apply(ParDo.of(new MapTransformation.TableRowTransformation(tableRowSuccessTag, tableRowFailedTag))
                .withOutputTags(tableRowSuccessTag, TupleTagList.of(tableRowFailedTag)));

        PCollection<TableRow> rowObj = tableRowTupleObj.get(tableRowSuccessTag).setCoder(NullableCoder.of(TableRowJsonCoder.of()));

        PCollection<string> failedobj = tableRowTupleObj.get(tableRowFailedTag).setCoder(NullableCoder.of(TableRowJsonCoder.of()));
        /*
         * Push records to BQ.
         */
        rowObj.apply(BigQueryIO.writeTableRows()
                .to("options.getOutputTable()"));

        //push failed records to BQ//
        failedobj.apply(BigQueryIO.writeTableRows().to(ErrorTable));



        pipeline.run().waitUntilFinish();
    }

    /*
     * Convert File to KV Object
     */
    private static class MapTransformation extends DoFn<FileIO.ReadableFile, KV<String, String>> {
        static final long serialVersionUID = 1L;
        TupleTag<KV<String, String>> successTag;
        TupleTag<KV<String, String>> failedTag;
        //TupleTag<String> failedFiles;
        //TupleTag<String> successFiles;


        public MapTransformation(TupleTag<KV<String, String>> successTag, TupleTag<KV<String, String>> failedTag) {
            this.successTag = successTag;
            this.failedTag = failedTag;

        }

        @ProcessElement
        public void processElement(ProcessContext c) throws IOException {
            FileIO.ReadableFile f = c.element();
            String fileName = f.getMetadata().resourceId().toString();
            String fileData = null;

            try {
                fileData = f.readFullyAsUTF8String();

                c.output(successTag, KV.of(fileName, fileData));
                // "dump_04_05_2022.csv", "'Ram','+919992220123','23','M','Sham','+919991110123','24','M',"
                //success files

            }
            catch (Exception e) {

                c.output(failedTag, KV.of((LocalDateTime.now().atZone(ZoneOffset.UTC).toString()), fileData));

                //failed files
                // Failed file name sample : 2022_06_23T17:51:51_name_of_your_file.csv,
                // "'anee','+919992220123','29','f','Sham','+919991110123','24','M'

                // "ArrayIndexOutOfBound Exception", "dump_02_02_2022.docs"
            }
        }

        /*
         * Convert KV to Table Row
         */

        private static class TableRowTransformation extends DoFn<KV<String, String>, TableRow> {
            static final long serialVersionUID = 1L;
            TupleTag<TableRow> successTag;
            TupleTag<TableRow> failedTag;

            public TableRowTransformation(TupleTag<TableRow> successTag, TupleTag<TableRow> failedTag) {
                this.successTag = successTag;
                this.failedTag = failedTag;
            }



            @ProcessElement
            public void processElement(ProcessContext c) {
                try {
                    KV<String, String> kvObj = c.element();
                    TableRow tableRow = new TableRow();
                    tableRow.set(kvObj.getKey(), kvObj.getValue());

                    c.output(successTag, tableRow);
                } catch (Exception e) {
                    TableRow tableRow = new TableRow();
                    tableRow.set("exception_type", e.getMessage());
                    tableRow.set("element", c.element());
                    tableRow.set("ingestion_timestamp", new DateTime());
                    c.output(failedTag, tableRow);
                }
            }
        }

    }
}
