# Entity Enrichment in MarkLogic Spring Batch

#### Background

The example is based on the Natural Language Processing (NLP) library of Apache
OpenNLP. The library has many capabilities for processing natural language text
like tokenization , sentence segmentation , part-of-speech tagging , named
entity extraction, chunking, parsing etc.

The example uses the tokenization and named entity extraction features of the
Apache OpenNLP. Tokenization involves segmenting the input character sequence
into tokens. In a given text , the tokenizer tokenizes the sentence into words,
punctuation etc. Out of several different tokenizers available , the example
uses the TokenizerME (the  learnable tokenizer) which returns an array of
Strings that make a sentence. Each string in the array is referred to as a
token.

The Name Finder can detect named entities and numbers in text. Given the array
of tokens , the Name Finder will generate markup for the person names in the
tokens which make up a sentence. In the example, the Name Finder api is used to
find the position(offset) of the person name within the array of tokens.

#### Setup

The example follows the standard Spring Batch model of defining Job
Configuration , ItemReader , ItemProcessor and ItemWrtiter. 

The EntityEnrichmentJobConfig is used to define the Job Configuration for batch
processing. It uses the DatabaseClientProvider (https://github.com/rjrudin/ml-javaclient-util) from the MarkLogic Java Client util to get connection to the
MarkLogic Database. It also takes the Job Parameters which include the path to
tokenizer model and named entity model.

The ItemReader uses the ValuesHandle (https://docs.marklogic.com/javadoc/client/) to read the combination of values (tuples) from the document.

The ItemWriter uses the DocumentPatchBuilder (https://docs.marklogic.com/javadoc) to patch the existing documents with additional(enriched) information.

The ItemProcessor processes the data using the tokenizer and name finder and
constructs a new document with the enriched data addendum to the original
document.

#### Workflow

The idea behind the entity enrichment example is to use the Apache OpenNLP
library to identify data of interest in the document and patch the data along
with the original document. The following text is the input to the tokenizer
which tokenizes based on the whitespace between the text.

``` xml
<doc>
    <text>Abbey D'Agostino finished the race Tuesday after helping Nikki Hamblin of New Zealand back up and urging her to finish. The two clipped heels during the late part of the race and tumbled to the ground. Hamblin has indicated she will run in the final.  Emma Coburn, who took bronze in the women's 3,000 steeplechase, becoming the first American woman to medal in the event, reacted Wednesday
     </text>
</doc>
```

The tokens are then passed on to the named entity model which identifies the position (offset) of names in the text.  

```
[Test worker] DEBUG e.EntityEnrichmentItemProcessor - Token Start: 46
[Test worker] DEBUG e.EntityEnrichmentItemProcessor - Token End: 47
[Test worker] INFO  e.EntityEnrichmentItemProcessor - Hamblin
[Test worker] DEBUG e.EntityEnrichmentItemProcessor - Token Start: 56
[Test worker] DEBUG e.EntityEnrichmentItemProcessor - Token End: 58
[Test worker] INFO  e.EntityEnrichmentItemProcessor - Emma Coburn
```

A new document is created from the identified names using the transformer which transforms the document to the standard XML format. Using the MarkLogic DocumentPatchBuilder (https://docs.marklogic.com/javadoc/client/com/marklogic/client/document/DocumentPatchBuilder.html), the original document is patched and written to the database. 

``` xml
<?xml  version="1.0" encoding="UTF-8"?>
<doc>
    <text>Abbey D'Agostino finished the race Tuesday after helping Nikki Hamblin of New Zealand back up and urging her to finish. The two clipped heels during the late part of the race and tumbled to the ground. Hamblin has indicated she will run in the final. Emma Coburn, who took bronze in the women's 3,000 steeplechase, becoming the first American woman to medal in the event, reacted Wednesday
    </text>
    <nameFinder>
        <name>Hamblin</name>
        <name>Emma Coburn</name>
    </nameFinder>
</doc>
```

In this way , using the best of Spring , Apache OpenNLP and MarkLogic the document content can be enhanced by extracting additional information , stored in MarkLogic using the Java Client API and queried using MarkLogic API. 