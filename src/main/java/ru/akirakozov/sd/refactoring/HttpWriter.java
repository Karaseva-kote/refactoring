package ru.akirakozov.sd.refactoring;

import java.io.PrintWriter;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class HttpWriter {
	public static void doAddProductResponse(PrintWriter writer) {
		writer.println("OK");
	}

	public static void doGetProductResponse(PrintWriter writer, List<Map.Entry<String, Integer>> result) {
		responseTemplate(
				writer,
				w -> {
					for (Map.Entry<String, Integer> entry : result) {
						String name = entry.getKey();
						int price = entry.getValue();
						w.println(name + "\t" + price + "</br>");
					}
				}
		);
	}

	public static void doMaxProductResponse(PrintWriter writer, Map.Entry<String, Integer> result) {
		doMaxMinProductResponse(writer, result, "max");
	}

	public static void doMinProductResponse(PrintWriter writer, Map.Entry<String, Integer> result) {
		doMaxMinProductResponse(writer, result, "min");
	}

	public static void doSumProductResponse(PrintWriter writer, int result) {
		doSumCountProductResponse(writer, result, "Summary price: ");
	}
	public static void doCountProductResponse(PrintWriter writer, int result) {
		doSumCountProductResponse(writer, result, "Number of products: ");
	}

	private static void doMaxMinProductResponse(PrintWriter writer, Map.Entry<String, Integer> result, String command) {
		responseTemplate(
				writer,
				w -> {
					w.println("<h1>Product with " + command + " price: </h1>");
					if (result != null) {
						String name = result.getKey();
						int price = result.getValue();
						w.println(name + "\t" + price + "</br>");
					}
				}
		);
	}

	private static void doSumCountProductResponse(PrintWriter writer, int result, String header) {
		responseTemplate(
				writer,
				w -> {
					w.println(header);
					w.println(result);
				}
		);
	}

	private static void start(PrintWriter writer) {
		writer.println("<html><body>");
	}

	private static void end(PrintWriter writer) {
		writer.println("</body></html>");
	}

	private static void responseTemplate(PrintWriter writer, Consumer<PrintWriter> responseBody) {
		start(writer);
		responseBody.accept(writer);
		end(writer);
	}
}
