package com.dxc.pipeline;

import org.apache.beam.sdk.Pipeline;
import org.apache.beam.sdk.io.TextIO;
import org.apache.beam.sdk.options.PipelineOptions;
import org.apache.beam.sdk.options.PipelineOptionsFactory;
import org.apache.beam.sdk.transforms.*;
import org.apache.beam.sdk.values.KV;
import org.apache.beam.sdk.values.PCollection;
import org.apache.beam.sdk.values.TypeDescriptors;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class wordcountpipeline {

    public static class CountWord extends DoFn<String, String> {

        @ProcessElement
        public void processElement(ProcessContext c) {

            Object inputObj = c.element();

            String[] b = String.valueOf(inputObj).split("[^a-zA-Z]");
            Map<String, Integer> map = new HashMap<>();
            for (int i = 0; i < b.length; i++) {

                if (!map.containsKey(b[i])) {

                    map.put(b[i], 1);
                } else {
                    map.put(b[i], map.get(b[i]) + 1);
                }
                //KV.of(b[i],map.get(b[i]) + 1);
            }
            c.output(map.toString());
            System.out.print(map);
        }
    }

    public static void main(String[] args) {
        String InputFile = "C:\\dataflow\\wcInput.txt";
        String OutputFile = "C:\\dataflow\\wcOutput3.txt";
        String OutputFile1 ="c:\\dataflow\\wcOutput1.txt";

        PipelineOptions options = PipelineOptionsFactory.fromArgs(args).withValidation().create();

        Pipeline p = Pipeline.create(options);

        PCollection<String> word = p.apply("ReadLines", TextIO.read().from(InputFile));

        word.apply("count the words", ParDo.of(new CountWord()))
        .apply("WriteCounts", (TextIO.write().to(OutputFile)));

        word.apply(FlatMapElements.into(TypeDescriptors.strings())
                                .via((String line) -> Arrays.asList(line.split("[^\\p{L}]+"))))

                .apply(Filter.by((String word2) -> !word2.isEmpty()))
                .apply(Count.perElement())
                .apply(
                        MapElements.into(TypeDescriptors.strings())
                                .via(
                                        (KV<String, Long> wordCount) ->
                                                wordCount.getKey() + ": " + wordCount.getValue()))
                .apply(TextIO.write().to(OutputFile1));
        p.run();
    }

}



