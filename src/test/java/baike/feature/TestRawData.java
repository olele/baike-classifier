package baike.feature;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.junit.Assert;
import org.junit.Test;

import baike.common.DataLoader;

public class TestRawData {
	@Test
	public void testLoadFromFile() throws IOException {
		InputStream rawDataIn = DataLoader.getInputStream("baidu-dump.dat.5000");
		RawData.Loader loader = new RawData.Loader(rawDataIn);
		int expectedIndex = 1;
		RawData raw = new RawData();
		while(loader.next(raw)) {
			Assert.assertEquals("" + expectedIndex, raw.get("ID"));
			expectedIndex ++;
		}
	}
	
	@Test
	public void testLoadFromString() throws IOException {
		String[] keys = new String[] {
			"ID", "Title", "RelatedTerm", "URL", "FileLOC", "Abstract", 
			"InnerLink", "ExternalTerm", "Category", "Image", "Fulltext"
		};
		String[] vals = new String[] {
			"32",
			"三级跳",
			"跳远::;跳高::;铁人三项::;蛙跳::;女子铅球::;径赛::;110米栏::;铅球::;短跑::;百米::;立定跳远::;竞走::;标枪::;铁饼::;实心球::;撑竿跳高::;链球",
			"302374.html",
			"302374.html",
			"三级跳又称为三级跳远，是田径中的其中一个项目之一。三级跳远起源于18世纪中叶的苏格兰和爱尔兰，男子三级跳远于1896年被列为首届奥运会比赛项目，女子三级跳远于20世纪80年代初逐渐广泛开展，1992年被列为奥运会比赛项目。",
			"三级跳远::;苏格兰::;爱尔兰::;苏格兰::;爱尔兰::;跳跃::;奥运会::;日本::;巴西::;苏联::;波兰::;英国::;比如::;沙坑::;助跑::;运动员::;木料::;手机::;比蒂::;1945年::;1968年::;1972年::;1976年",
			"三级跳远项目特点=http://dating.taobao.com/q/5340445.htm::;三级跳=http://data.sports.163.com/item/history/0005000CBRGI.html::;三级跳远运动员的弱腿训练=http://www.zghdjk.com/sports/67149.htm::;三级跳远=http://www.qm120.com/tags/%E4%B8%89%E7%BA%A7%E8%B7%B3%E8%BF%9C::;田径各分项目介绍=http://news.xinhuanet.com/sports/2010-04/16/c_1237950.htm::;田径=http://syh.syd.com.cn/content/2010-05/17/content_24760943.htm",
			"运动::;体育::;生活::;田径::;奥运会",
			"http://imgsrc.baidu.com/baike/abpic/item/79b1e9363ef72b710a55a9d2.jpg::;http://imgsrc.baidu.com/baike/abpic/item/1f5694826c5653d2f603a6bf.jpg::;http://imgsrc.baidu.com/baike/abpic/item/906289dd3e3b157e5882dd6f.jpg::;http://imgsrc.baidu.com/baike/abpic/item/5202e5f2259d2b59b17ec57c.jpg",
			"技巧　　第一，“积极起跳式”，这一技术风格的特点，是在第二跳和第三跳积极放腿起跳。为此目的，运动员在腾空阶段就开始准备起跳——空中屈腿抬膝。 然后快速伸腿着地，使第二跳的距离明显加长。其突出特点是第一跳距离较远，在放腿起跳前的腾空阶段，大腿举得较高（“高摆腿”）。然后由上向下，“冲击式”落腿着地，保障与支撑动作的积极协同。这种方式可以进一步延长第二跳的距离，但同时又会造成第三跳对总成绩的“贡献”有所下降。"
		};
		StringBuilder sb = new StringBuilder();
		assert(keys.length == vals.length);
		int numEntries = keys.length;
		for(int ei = 0; ei < numEntries; ei++) {
			sb.append(keys[ei]).append(':').append(vals[ei]).append('\n');
		}
		String strRawData = sb.toString();
		RawData.Loader loader = str2Raw(strRawData);
		RawData raw = new RawData();
		Assert.assertTrue(loader.next(raw));
		for(int ei = 0; ei < numEntries; ei++) {
			String key	 			 = keys[ei];
			String expectedVal = vals[ei];
			String actualVal	 = raw.get(key);
			Assert.assertEquals(expectedVal, actualVal);
		}
		Assert.assertFalse(loader.next(raw));
	}
	
	public static RawData.Loader str2Raw(String strRawData) {		
		ByteArrayInputStream rawDataIn = 
				new ByteArrayInputStream(strRawData.getBytes());
		return new RawData.Loader(rawDataIn);
	}
}
