package be.kpoint.pictochat.app.domain.serializer;

import java.lang.reflect.Type;
import java.util.Date;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class JsonDateSerializer implements JsonSerializer<Date>, JsonDeserializer<Date>
{
	@Override
	public Date deserialize(JsonElement json, Type type, JsonDeserializationContext context) throws JsonParseException
	{
		JsonObject jsonObject = json.getAsJsonObject();
	    JsonElement element = jsonObject.get("time");

	    return new Date(element.getAsLong());
	}

	@Override
	public JsonElement serialize(Date id, Type type, JsonSerializationContext context)
	{
		JsonObject result = new JsonObject();

		result.add("time", context.serialize(id.getTime()));

		return result;
	}
}
