package com.marklogic.spring.batch.jdbc.support.incrementer;

import java.util.concurrent.ThreadLocalRandom;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.support.incrementer.DataFieldMaxValueIncrementer;

public class UriIncrementer implements DataFieldMaxValueIncrementer {

	@Override
	public int nextIntValue() throws DataAccessException {
		return Math.abs(ThreadLocalRandom.current().nextInt());
	}

	@Override
	public long nextLongValue() throws DataAccessException {
		return Math.abs(ThreadLocalRandom.current().nextLong());
	}

	@Override
	public String nextStringValue() throws DataAccessException {
		// TODO Auto-generated method stub
		return null;
	}

}
