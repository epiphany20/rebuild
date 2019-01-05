/*
rebuild - Building your system freely.
Copyright (C) 2018 devezhao <zhaofang123@gmail.com>

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program. If not, see <https://www.gnu.org/licenses/>.
*/

package com.rebuild.server.business.series;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.rebuild.server.TestSupport;
import com.rebuild.server.metadata.MetadataHelper;

import cn.devezhao.commons.ThreadPool;
import cn.devezhao.persist4j.Field;

/**
 * 
 * @author devezhao
 * @since 12/24/2018
 */
public class SeriesGeneratorTest extends TestSupport {

	@Test
	public void testTimeVar() throws Exception {
		String r = new TimeVar("YYMMDD").generate();
		System.out.println(r);
	}
	
	@Test
	public void testIncrementVar() throws Exception {
		if (getSeriesField() == null) {
			return;
		}
		
		IncreasingVar var = new IncreasingVar("0000", getSeriesField(), null);
		System.out.println(var.generate());
		System.out.println(var.generate());
		System.out.println(var.generate());
	}
	
	@Test
	public void testIncrementVarNThreads() throws Exception {
		if (getSeriesField() == null) {
			return;
		}
		
		final IncreasingVar var = new IncreasingVar("0000", getSeriesField(), "Y");
		final Set<String> set = new HashSet<>();
		for (int i = 0; i < 2000; i++) {
			ThreadPool.exec(new Runnable() {
				@Override
				public void run() {
					String s = var.generate();
					set.add(s);
					System.out.print(s + " ");
				}
			});
		}
		ThreadPool.waitFor(1000);
		Assert.assertTrue(set.size() == 2000);
	}
	
	@Test
	public void testGenerate() throws Exception {
		if (getSeriesField() == null) {
			return;
		}
		
		Map<String, String> config = new HashMap<>();
		config.put("seriesFormat", "Y-{YYYYMMDD}-{0000}");
		config.put("seriesZero", "M");
		
		SeriesGenerator generator = new SeriesGenerator(getSeriesField(), (JSONObject) JSON.toJSON(config));
		System.out.println(generator.generate());
		System.out.println(generator.generate());
		System.out.println(generator.generate());
	}
	
	private Field getSeriesField() {
		if (!MetadataHelper.containsEntity("ceshiziduan")) {
			return null;
		}
		Field field = MetadataHelper.getEntity("ceshiziduan").getField("ZIDONGBIANHAO");
		return field;
	}
}
