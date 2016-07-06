package be.kpoint.pictochat.app.domain.serializer;

import java.lang.reflect.Type;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import be.kpoint.pictochat.api.rest.ids.AbstractId;

public class JsonUserIdSerializer implements JsonSerializer<AbstractId>, JsonDeserializer<AbstractId>
{
	@Override
	public AbstractId deserialize(JsonElement json, Type type, JsonDeserializationContext context) throws JsonParseException
	{
		JsonObject jsonObject = json.getAsJsonObject();
	    String className = jsonObject.get("type").getAsString();
	    JsonElement element = jsonObject.get("properties");

	    try {
	        return context.deserialize(element, Class.forName(className));
	    } catch (ClassNotFoundException ex) {
	        throw new JsonParseException("Unknown element type: " + type, ex);
	    }
	}

	@Override
	public JsonElement serialize(AbstractId id, Type type, JsonSerializationContext context)
	{
		JsonObject result = new JsonObject();

		result.add("type", new JsonPrimitive(id.getClass().getCanonicalName()));
		result.add("properties", context.serialize(id, id.getClass()));

		return result;
	}
}
