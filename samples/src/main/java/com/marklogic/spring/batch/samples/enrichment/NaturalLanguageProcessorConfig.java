package com.marklogic.spring.batch.samples.enrichment;

import opennlp.tools.tokenize.Tokenizer;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by sstafford on 8/16/2016.
 */
@Configuration
public class NaturalLanguageProcessorConfig {

    @Bean
    public Tokenizer getTokenizer() throws FileNotFoundException, IOException {
        InputStream modelIn = new FileInputStream("src/main/resources/nlp/tokenizer/en-token.bin");
        TokenizerModel model = new TokenizerModel(modelIn);
        return new TokenizerME(model);
    }

}
