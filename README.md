#LOMI - Enrich Linked Open Data (DBpedia) with Microdata

This project was created during a one-year project at the [Universitiy of Mannheim](https://www.uni-mannheim.de/1/). There has been efforts to [Common Crawl](http://commoncrawl.org/), a project which provides an "open repository of web crawl data that can be accessed and analyzed by anyone". This data is used by the [Web Data Commons](http://webdatacommons.org/) project to extract [Schema.org data in N-Quads format](http://webdatacommons.org/structureddata/2014-12/stats/schema_org_subsets.html).

The main startup classes are located under "com.maximilian_boehm.lod.main". Ideally, you will need to assign at least 6 GB to the JVM to get results. The program is separated in three phases. Phase 1 is the deduper (A0_Deduper.java) which finds instance with multiple occurences and reduces the occurences to a single one. In phase 2, the transformer (A1_Transformer.java) transforms the instances from the Schema.org-Vocabulary to the dbpedia ontology. And finally in phase 3, the instance matcher (A2_InstanceMatcher.java) finds corresponding matches between data from the web and dbpedia. 

See also my [blog post](https://www.maximilian-boehm.com/hp2125/LOMI-Enrich-Linked-Open-Data-DBpedia-with-Microdata.htm) for further explanations.
