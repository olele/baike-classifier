package baike.nlp;

import java.util.List;

import org.ansj.domain.Nature;
import org.ansj.domain.Term;
import org.ansj.splitWord.analysis.ToAnalysis;
import org.ansj.util.recognition.NatureRecognition;
import org.junit.Test;

public class TestPartOfSpeech {
	@Test
	public void test() {
		String text = "学校简介学校介绍　　吴泾中学由总部（原吴泾中学）和分部（原吴泾二中）组成，学校于2002年9月合并后，对所有校舍和操场进行彻底的整修，教室明亮，环境幽雅。学校设备　　现总部有28个教室，24个教学班，分部有15个教室，6个教学班。各种专用教室配备齐全：2个电脑房、理化生实验室各2个、音乐室、美术室各1个、电子语音/阅览室、图书馆和阶梯教室。各教室最近又装置了先进的投影设备；250M 塑胶跑道及草皮球场近日完工已投入使用。学校有宿舍、食堂、浴室等生活实施健全。学校地处闵行区东南方位，东临黄浦江，南对正在开发的紫竹科技园区和交大大学城，西靠闵行经济开发区，北面与徐汇区接界，一条六车道的龙吴路将龙华与吴泾接通，不足30分钟的时间，乘车可达闹市区。节日活动　　每年有读书节、科技节、心理周、感恩节、运动会、艺术节、迎新会等大型活动。规模队伍　　目前本校现有班级33个，在校学生数为1277人，有107名教师，学历全部达标或超标，其中中高级职称的教师比例占到75%，而且大部分为中青年教师，其中有研究生8名，是一支实力雄厚的师资队伍。学校文化　　学校在“团结、奋发、求实、竞争、创新”的校训指导下，在“严谨、热情、生动、活泼”教风的熏陶下，形成了“勤奋、求实、互助、竞争”的学风。学校领导和全体师生员工精神振奋，开拓进取，勇于创新，在学校管理、德育工作、教学改革等方面创造了光辉的昨天。高考录取率、录取质量逐年提高，年年超额完成教学质量指导性目标，完成率在闵行区中名列前茅。特色项目　　足球运动是我校的体育特色，历年来多次荣获区市级的各种奖项，为本校增添光彩，同时，我校是闵行区的体育特色学校。";		
		// word segmentation
		List<Term> paser = ToAnalysis.paser(text);
		// part of speech
		new NatureRecognition(paser).recognition() ;
	
		int index = 0;
		int count = paser.size();
		while(index < count) {
			Term t = paser.get(index);
			Nature n = t.getNatrue();
			System.out.println(n.natureStr);
			index ++;
		}
	}

	@Test
	public void test2() {
		String text = "【相对分子量或原子量】158.00　　【密度】1.657（15℃）　　【沸点（℃）】193～194（101.858E3Pa)；74～75(1.733E3Pa)　　【闪点（℃）】54　　【折射率】1.5734　　【性状】　　浅黄色油状液体。　　【溶解情况】　　微溶于水，与乙醇、乙醚、苯和吡啶混溶。　　【物化性质】(Physical Properties) 　　,BP 192-194℃,74-75（13mmHg),nD201. 5720,d4201.634,fp 54,能与乙醇、乙醚和吡啶混溶.在空气中敏感,见光颜色变深。有毒及刺激性，易燃 .6.1/Ⅱ UN2929。 　　【用途】　　用作医药、农药及有机合成中间体。　　【制备或来源】　　（1）以邻氨基吡啶为原料，溶于氢溴酸中，在0℃及搅拌下，缓缓通入溴素，反应完成后，再加入亚硝酸钠溶液，过滤，再加入氢氧化钠溶液中和，分出有机层，用乙醚萃取，蒸出乙醚后，残液采用减压蒸馏（3.333E3Pa)而得。　　（2）以吡啶为原料，在溴化亚铜作用下，在300～400℃下与溴反应而得。";
		// word segmentation
		List<Term> paser = ToAnalysis.paser(text);
		// part of speech
		new NatureRecognition(paser).recognition() ;
		
		System.out.println(paser);
	}
}
