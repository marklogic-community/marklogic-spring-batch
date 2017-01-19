*The MarkLogic JobRepository is in beta and should not be used for production.*    


has extended the [JobRepository](http://docs.spring.io/spring-batch/trunk/reference/html/domain.html#domainJobRepository) to persist the job execution metadata into MarkLogic.  


# How can I use the MarkLogic Job Repository?
If you want to use a MarkLogic Job Repository then you first need to install a new application onto your MarkLogic database.  A MarkLogic Job Repository can be installed via the executable created with your Gradle application plugin.  

```
./<jobArtifactScriptName> deployMarkLogicJobRepository --jr_host localhost --jr_port 8011 --jr_username admin --jr_password admin
```

If you ever need to undeploy the JobRepository then you can issue the following command.

```
./<jobArtifactScriptName> undeployMarkLogicJobRepository --jr_host localhost --jr_port 8011 --jr_username admin --jr_password admin
```

Now when you execute your job, then add the additional parameters for the MarkLogic Job Repository.  All JobExecution metadata is now logged to the MarkLogic JobRepository.  

```
./<jobArtifactScriptName> --config com.marklogic.spring.batch.job.JobNameConfig --host localhost --port 8010 --username admin --password admin --custom_param1 xyz --customParamX abc --jr_host localhost --jr_port 8011 --jr_username admin --jr_password admin
```