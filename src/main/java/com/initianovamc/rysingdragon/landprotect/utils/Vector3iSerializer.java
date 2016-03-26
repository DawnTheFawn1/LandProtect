package com.initianovamc.rysingdragon.landprotect.utils;

import com.flowpowered.math.vector.Vector3i;
import com.google.common.reflect.TypeToken;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializer;

public class Vector3iSerializer implements TypeSerializer<Vector3i>{

	@Override
	public Vector3i deserialize(TypeToken<?> type, ConfigurationNode value) throws ObjectMappingException {
		String deserialized = value.getString();
		int first = deserialized.indexOf(",");
		int last = deserialized.lastIndexOf(",");
		String stringX = deserialized.substring(0, first);
		String StringY = deserialized.substring(first+2, last);
		String StringZ = deserialized.substring(last+2);
		int x = Integer.parseInt(stringX);
		int y = Integer.parseInt(StringY);
		int z = Integer.parseInt(StringZ);
		
		return new Vector3i(x, y, z);
	}

	@Override
	public void serialize(TypeToken<?> type, Vector3i obj, ConfigurationNode value) throws ObjectMappingException {
		String serialized = obj.getX() + ", " + obj.getY() + ", " + obj.getZ();
		value.setValue(serialized);
	}

}
