/*
 * Copyright (c) 2016-2017, Adam <Adam@sigterm.info>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package net.runelite.cache.definitions.exporters;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import net.runelite.cache.definitions.ItemDefinition;

public class ItemExporter
{
	private final ItemDefinition item;
	private final Gson gson;

	public ItemExporter(ItemDefinition item)
	{
		this.item = item;

		GsonBuilder builder = new GsonBuilder()
			.setPrettyPrinting()
			.setExclusionStrategies(new ExclusionStrategy()
			{
				@Override
				public boolean shouldSkipField(FieldAttributes f)
				{
					try
					{
						// Skip wearPos fields if they have value -1 (uninitialized)
						String fieldName = f.getName();
						if (fieldName.equals("wearPos1") || fieldName.equals("wearPos2") || fieldName.equals("wearPos3"))
						{
							Object value = f.getDeclaringClass().getField(fieldName).get(item);
							return value instanceof Integer && (Integer) value == -1;
						}
					}
					catch (Exception e)
					{
						// If we can't access the field, don't skip it
					}
					return false;
				}

				@Override
				public boolean shouldSkipClass(Class<?> clazz)
				{
					return false;
				}
			});
		gson = builder.create();
	}

	public String export()
	{
		return gson.toJson(item);
	}

	public void exportTo(File file) throws IOException
	{
		try (FileWriter fw = new FileWriter(file))
		{
			fw.write(export());
		}
	}
}
