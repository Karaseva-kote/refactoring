package ru.akirakozov.sd.refactoring;

import org.apache.commons.text.StringSubstitutor;
import org.apache.log4j.Logger;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

public class ProductDao {
	private static final Logger log = Logger.getLogger(ProductDao.class);
	private static final SqlCompanion SQL = SqlCompanion.forClass(ProductDao.class);
	private static final String createProductQuery = SQL.getRequiredQuery("CREATE_PRODUCT_QUERY");
	private static final String insertProductQuery = SQL.getRequiredQuery("INSERT_PRODUCT_QUERY");
	private static final String selectProductQuery = SQL.getRequiredQuery("SELECT_PRODUCT_QUERY");
	private static final String selectMaxProductQuery = SQL.getRequiredQuery("SELECT_MAX_PRODUCT_QUERY");
	private static final String selectMinProductQuery = SQL.getRequiredQuery("SELECT_MIN_PRODUCT_QUERY");
	private static final String selectSumProductQuery = SQL.getRequiredQuery("SELECT_SUM_PRODUCT_QUERY");
	private static final String selectCountProductQuery = SQL.getRequiredQuery("SELECT_COUNT_PRODUCT_QUERY");
	private static final String clearProductQuery = SQL.getRequiredQuery("CLEAR_PRODUCT_QUERY");

	private static final String dbUrl = "jdbc:sqlite:test.db";

	private static void doActionDB(ThrowingConsumer<Statement> consumer) throws Exception {
		try (Connection c = DriverManager.getConnection(dbUrl)) {
			try (Statement statement = c.createStatement()) {
				consumer.accept(statement);
			}
		}
	}

	public static void createDB() throws Exception {
		doActionDB(statement -> statement.executeUpdate(createProductQuery));
	}

	public static void insertProduct(String name, Long price) throws Exception {
		Map<String, Object> substitutions = Map.of(
				"name", name,
				"price", price
		);
		String query = new StringSubstitutor(substitutions, "{{", "}}").replace(insertProductQuery);
		log.info(query);
		doActionDB(statement -> statement.executeUpdate(query));
	}

	public static List<Map.Entry<String, Integer>> getProducts() throws Exception {
		List<Map.Entry<String, Integer>> result = new ArrayList<>();
		doActionDB(statement -> {
			ResultSet rs = statement.executeQuery(selectProductQuery);
			while (rs.next()) {
				String name = rs.getString("name");
				int price = rs.getInt("price");
				result.add(Map.entry(name, price));
			}
		});
		return result;
	}

	private static Map.Entry<String, Integer> findMaxOrMin(String query) throws Exception {
		AtomicReference<Map.Entry<String, Integer>> result = new AtomicReference<>(null);
		doActionDB(statement -> {
			ResultSet rs = statement.executeQuery(query);
			while (rs.next()) {
				String name = rs.getString("name");
				int price = rs.getInt("price");
				result.set(Map.entry(name, price));
			}
		});
		return result.get();
	}

	public static Map.Entry<String, Integer> findMax() throws Exception {
		return findMaxOrMin(selectMaxProductQuery);
	}

	public static Map.Entry<String, Integer> findMin() throws Exception {
		return findMaxOrMin(selectMinProductQuery);
	}

	private static int findSumOrCount(String query) throws Exception {
		AtomicInteger result = new AtomicInteger();
		doActionDB(statement -> {
			ResultSet rs = statement.executeQuery(query);
			if (rs.next()) {
				result.set(rs.getInt(1));
			}
		});
		return result.get();
	}

	public static int findSum() throws Exception {
		return findSumOrCount(selectSumProductQuery);
	}

	public static int findCount() throws Exception {
		return findSumOrCount(selectCountProductQuery);
	}

	public static void clearDB() throws Exception {
		doActionDB(statement -> statement.executeUpdate(clearProductQuery));
	}

	public static void executeQuery(String query) throws Exception {
		doActionDB(statement -> statement.executeUpdate(query));
	}

	@FunctionalInterface
	public interface ThrowingConsumer<T> extends Consumer<T> {

		@Override
		default void accept(final T elem) {
			try {
				acceptThrows(elem);
			} catch (final Exception e) {
				log.error("Exception (" + e + ") -> RuntimeException");
				throw new RuntimeException(e);
			}
		}

		void acceptThrows(T elem) throws Exception;
	}
}
