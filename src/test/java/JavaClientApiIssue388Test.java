import org.junit.Test;

import com.marklogic.client.query.StructuredQueryBuilder;
import com.marklogic.client.query.StructuredQueryDefinition;
import com.marklogic.junit.Fragment;
import com.marklogic.spring.batch.AbstractSpringBatchTest;

public class JavaClientApiIssue388Test extends AbstractSpringBatchTest {
	
	@Test
	public void replicateIssue388Test() {
		StructuredQueryBuilder qb = new StructuredQueryBuilder("default");
		StructuredQueryDefinition querydef = 
			    qb.and(qb.term("neighborhood"), 
			           qb.valueConstraint("industry", "Real Estate"));
		Fragment frag = new Fragment(querydef.serialize(), getNamespaceProvider().getNamespaces());
		frag.assertElementValue("//search:text", "neighborhood");
		System.out.println(frag.getPrettyXml());
	}
}
