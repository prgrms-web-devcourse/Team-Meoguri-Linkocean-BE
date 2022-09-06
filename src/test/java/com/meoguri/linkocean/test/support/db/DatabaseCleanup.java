package com.meoguri.linkocean.test.support.db;

import static org.apache.commons.lang3.reflect.FieldUtils.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.CollectionTable;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Table;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class DatabaseCleanup implements InitializingBean {

	@PersistenceContext
	private EntityManager entityManager;

	private List<String> tableNames = new ArrayList<>();

	@Transactional
	public void execute() {

		entityManager.flush();
		entityManager.createNativeQuery("SET FOREIGN_KEY_CHECKS = 0").executeUpdate();

		for (final String tableName : tableNames) {
			entityManager.createNativeQuery("TRUNCATE TABLE " + tableName).executeUpdate();
		}

		entityManager.createNativeQuery("SET FOREIGN_KEY_CHECKS = 1").executeUpdate();
	}

	@Override
	public void afterPropertiesSet() throws Exception {

		tableNames.addAll(getEntityTableNames());
		tableNames.addAll(getCollectionTableNames());
	}

	private List<String> getCollectionTableNames() {

		return entityManager.getMetamodel().getEmbeddables().stream()
			.flatMap(e -> Arrays.stream(getAllFields(e.getJavaType())))
			.filter(f -> f.getAnnotation(CollectionTable.class) != null)
			.map(f -> f.getAnnotation(CollectionTable.class).name())
			.collect(Collectors.toList());
	}

	private List<String> getEntityTableNames() {

		return entityManager.getMetamodel().getEntities().stream()
			.filter(e -> e.getJavaType().getAnnotation(Table.class) != null)
			.map(e -> e.getJavaType().getAnnotation(Table.class).name())
			.collect(Collectors.toList());
	}
}
