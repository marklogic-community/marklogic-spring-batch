package com.marklogic.spring.batch.item.tasklet;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.datamovement.DataMovementManager;
import com.marklogic.client.datamovement.DeleteListener;
import com.marklogic.client.datamovement.JobTicket;
import com.marklogic.client.datamovement.QueryBatcher;
import com.marklogic.client.document.GenericDocumentManager;
import com.marklogic.client.ext.helper.DatabaseClientProvider;
import com.marklogic.client.io.SearchHandle;
import com.marklogic.client.query.MatchDocumentSummary;
import com.marklogic.client.query.QueryManager;
import com.marklogic.client.query.StructuredQueryDefinition;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;

public class DeleteDocumentsTasklet implements Tasklet {

    private DatabaseClient databaseClient;
    private StructuredQueryDefinition queryDefinition;
    private String marklogicVersion = "8";

    public DeleteDocumentsTasklet(DatabaseClientProvider databaseClientProvider, StructuredQueryDefinition queryDef) {
        this.databaseClient = databaseClientProvider.getDatabaseClient();
        this.queryDefinition = queryDef;
    }

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
        if (marklogicVersion.equals("8")) {
            QueryManager qryMgr = databaseClient.newQueryManager();
            GenericDocumentManager docMgr = databaseClient.newDocumentManager();
            SearchHandle handle = qryMgr.search(queryDefinition, new SearchHandle());
            MatchDocumentSummary[] results = handle.getMatchResults();
            for (int i = 0; i < results.length; i++) {
                docMgr.delete(results[i].getUri());
            }
        } else if (marklogicVersion.equals("9")) {
            final DataMovementManager dataMovementManager = databaseClient.newDataMovementManager();
            QueryBatcher qb = dataMovementManager.newQueryBatcher(queryDefinition)
                    .withBatchSize(2500)
                    .withConsistentSnapshot()
                    .onUrisReady(new DeleteListener())
                    .onQueryFailure(throwable -> throwable.printStackTrace());
            JobTicket ticket = dataMovementManager.startJob(qb);
            qb.awaitCompletion();
            dataMovementManager.stopJob(ticket);

        }
        return RepeatStatus.FINISHED;
    }


    public void setMarklogicVersion(String marklogicVersion) {
        this.marklogicVersion = marklogicVersion;
    }
}
