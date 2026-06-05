package storage;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;

import catalog.ColumnSchema;
import catalog.TableSchema;

public class Serializer {

	public static byte[] serialize(Tuple tuple, TableSchema schema) {
		List<ColumnSchema> columns = schema.getColumns();
		int totalSize = 0;
		int count = 0;

		for (ColumnSchema column : columns) {

			if (!column.getType().isVariable()) {
				totalSize += column.getType().getFixedSize();
			} else {
				String str = (String) tuple.getValue(count);
				byte[] strBytes = str.getBytes(StandardCharsets.UTF_8);
				totalSize += 4 + str.length();
			}
			count++;
		}

		ByteBuffer buffer = ByteBuffer.allocate(totalSize);

		count = 0;
		for (ColumnSchema columnSchema : columns) {
			Object value = tuple.getValue(count);

			switch (columnSchema.getType()) {
			case INT: {
				buffer.putInt((Integer) value);
				break;
			}
			case BOOLEAN: {
				buffer.put((byte) ((Boolean) value ? 1 : 0));
				break;
			}
			case BIGINT: {
				buffer.putLong((long) value);
				break;
			}
			case TIMESTAMP: {
				long timeMilis = ((java.util.Date) value).getTime();
				buffer.putLong(timeMilis);
				break;
			}
			case UUID: {
				UUID uuid = (UUID) value;
				buffer.putLong(uuid.getMostSignificantBits());
				buffer.putLong(uuid.getLeastSignificantBits());
				break;
			}
			case VARCHAR: {
				String str = (String) value;
				byte[] strBytes = str.getBytes(StandardCharsets.UTF_8);
				buffer.putInt(strBytes.length);
				buffer.put(strBytes);
			}
			}
			count++;
		}
		return buffer.array();
	}

	public static Tuple deserialize(byte[] recordByte, TableSchema schema) throws Exception {
		if (recordByte == null)
			return null;

		ByteBuffer buffer = ByteBuffer.wrap(recordByte);
		List<ColumnSchema> columns = schema.getColumns();
		int count = 0;

		Object[] values = new Object[columns.size()];

		for (ColumnSchema column : columns) {
			switch (column.getType()) {
			case INT: {
				values[count] = buffer.getInt();
				break;
			}
			case BOOLEAN: {
				values[count] = buffer.get() == 1;
				break;
			}
			case BIGINT: {
				values[count] = buffer.getLong();
				break;
			}
			case TIMESTAMP: {
				long timeMillis = buffer.getLong();
				values[count] = new java.util.Date(timeMillis);
				break;
			}
			case UUID: {
				long mostSigBits = buffer.getLong();
				long leastSigBits = buffer.getLong();

				values[count] = new UUID(mostSigBits, leastSigBits);
				break;
			}
			case VARCHAR: {
				int length = buffer.getInt();
				byte[] strByte = new byte[length];
				buffer.get(strByte);
				values[count] = new String(strByte, StandardCharsets.UTF_8);
				break;
			}
			default:
				throw new Exception("Tipo de dado não identificado.");
			}
			count++;
		}
		return new Tuple(values);
	}
}
