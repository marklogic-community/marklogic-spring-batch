package com.marklogic.spring.batch.item.tasklet;

import com.marklogic.client.helper.DatabaseClientProvider;
import com.marklogic.client.query.QueryDefinition;
import com.marklogic.datamovement.DataMovementManager;
import com.marklogic.datamovement.JobTicket;
import com.marklogic.datamovement.QueryHostBatcher;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;

public class DeleteDocumentsTasklet implements Tasklet {

    private DatabaseClientProvider databaseClientProvider;
    private QueryDefinition queryDefinition;

    public DeleteDocumentsTasklet(DatabaseClientProvider databaseClientProvider, QueryDefinition queryDef) {
        this.databaseClientProvider = databaseClientProvider;
        this.queryDefinition = queryDef;
    }

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
        final DataMovementManager dataMovementManager = DataMovementManager.newInstance();
        dataMovementManager.withClient(databaseClientProvider.getDatabaseClient());
        QueryHostBatcher qhb = dataMovementManager.newQueryHostBatcher(queryDefinition)
                .withBatchSize(5)
                .withThreadCount(2)
                .withConsistentSnapshot()
                .onUrisReady((client, batch) -> {
                    for ( String uri : batch.getItems() ) {
                        client.newDocumentManager().delete(uri);
                    }
                })
                .onQueryFailure((client, exception) -> exception.printStackTrace());
        JobTicket ticket = dataMovementManager.startJob(qhb);
        qhb.awaitCompletion();
        dataMovementManager.stopJob(ticket);
        return RepeatStatus.FINISHED;
    }

}
