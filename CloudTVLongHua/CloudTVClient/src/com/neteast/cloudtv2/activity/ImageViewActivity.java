package com.neteast.cloudtv2.activity;

import android.app.Activity;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.neteast.cloudtv2.R;

/** @author LeonZhang E-mail: zhanglinlang1@163.com
 * @createtime 2013-3-25 */
public class ImageViewActivity extends Activity {

	private ImageView imageView;
	private TextView mTextView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.image_view_layout);
		initLayout();
		initData();
	}

	private void initLayout() {
		imageView = (ImageView) findViewById(R.id.image_view_v);
		mTextView = (TextView) findViewById(R.id.content_text);
	}

	private void initData() {
		int id = getIntent().getIntExtra("service", 1);
		imageView.setImageResource(id);
		String type = getIntent().getStringExtra("type");
		if ("desc".equals(type)) {
			mTextView
					.setText(Html
							.fromHtml("<H1><font color=\"blue\" align=\"center\" >&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;" +
									"重庆医科大学附属第一医院医院集团</font></H1><br/><br/>"
									+ "重庆医科大学附属第一医院<br/><br/>"
									+ "<H5>简 介:</H5><br/>"
									+ "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;重庆医科大学附属第一医院于1957年由上海第一医学院（现复旦大学医学院）分迁来渝创建，历经50余年的建设和发展，已成为全国首批“三级甲等医院”和重庆市规模最大、设备最先进、技术实力最强，融医疗、教学、科研、预防、保健及涉外医疗为一体的重点大型综合性教学医院。医院牵头成立了重庆市首家医院集团，即“重医一院医院集团”，集团以院本部为核心医院，包括3家直属分院（第一分院、金山医院、青杠老年护养中心）、6家托管医院（大足区人民医院、海扶医院、綦江区人民医院、万盛经开区人民医院、酉阳县人民医院、合川区人民医院）。 <br/><br/>"
									+ "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;服务能力出众  院本部开设临床科室35个，医技科室8个，现有编制床位3200张，2012年门诊量230.51万人次，出院病人9.75万人次，年手术3.8万台次，危重病人抢救成功率达90%以上。病人来自全国各地及美国、澳大利亚、日本等国。在重庆及四川、贵州等周边省市拥有20家教学医院和73家指导医院。<br/><br/>"
									+ "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;医疗技术精湛  院本部常年承担疑难危急重症救治、应急救援、干部保健及涉外医疗任务，是中国西部疑难重症诊疗中心，重庆市SARS、甲型H1N1流感等重大传染病重症病例救治首要单位和专家组长单位，以及重庆市唯一一家同时获肝、肾移植技术准入的地方医院。形成了器官移植、微创、介入、辅助生殖、无痛诊疗等优势技术。在卫生部2010年至2012年“国家临床重点专科建设”评估中，有14个专科进入国家临床重点专科建设项目（产科、检验科、重症医学、专科护理、内分泌科、心血管内科、胸外科、神经外科、麻醉科、神经内科、普通外科、眼科、呼吸内科、耳鼻咽喉科）。获批卫生部培训基地8个，重庆市临床诊疗中心25个，重庆市医疗质量控制中心16个，重庆市专科护士培训基地7个。各学科均形成了自身的特色优势，综合诊断治疗水平居国内先进行列。<br/><br/>"
									+ "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;医疗设备先进  院本部拥有PET-CT、炫速双源CT、64排螺旋CT、3.0T MRI、1.5TMRI、DSA、高强度超声聚焦肿瘤治疗系统、影像引导放射治疗系统（RGRT）等国内一流的大型医疗设备。<br/><br/>"
									+ "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;医学人才济济  院本部拥有973首席科学家、国家杰出青年、国务院学位委员会学科评议组成员、长江学者、卫生部突出贡献专家等在内的一大批学术造诣高、临床经验丰富的知名专家。拥有教授230人，副教授345人，博士生导师43人，硕士生导师256人，享受政府津贴专家43人，重庆市学术技术带头人及后备人选37人，重庆市医学会主任、副主任委员49人。<br/><br/>"
									+ "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;科教成效显著  院本部形成了以神经科学、肿瘤学、脂糖代谢、眼科及免疫学等为代表的“五大优势学科群”。设有临床医学博士后流动站，博士学位授权点32个、硕士学位授权点35个。已建成国家级重点学科2个（内科学-传染病、神经病学）、国家中医药重点学科1个（中西医结合临床）,重庆市高校重点学科34个、重庆市卫生局医学重点学科11个,重庆市重点实验室5个。承办统计源期刊《中华内分泌外科杂志》。近5年（2008—2012）来承担包括国家973项目、国家高新技术研究发展计划(863计划)、国家自然科学基金重点项目、“十一五”国家科技重大专项计划等各级纵向科研课题761项,获得国家科技进步二等奖、重庆市科技突出贡献奖在内的各级科技成果奖59项,在科技统计源期刊上发表的科技论文数居全国医疗机构第6位。<br/><br/>"
									+ "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;荣誉奖项众多 院本部先后获得“全国百佳医院”、“全国卫生系统先进集体”、“全国医院文化建设先进单位”、“全国卫生系统思想政治工作先进单位”、中华全国总工会“工人先锋号”等一系列荣誉称号。2008年荣获卫生部“2005-2007年度全国医院管理年活动先进单位”和“全国卫生系统护士岗位技能竞赛”金奖。<br/><br/>"
									+ "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;重庆医科大学附属第一医院第一分院 （原重庆市第八人民医院，经重庆市人民政府批准，于2010年11月2日成建制并入重庆医科大学附属第一医院）是一所以医疗为中心，兼有科研、教学和干部保健等职能的国家二级甲等综合医院。承担中央领导来渝的医疗保健任务和省部级、厅局级领导的日常主动保健工作，以及社区居民22万人的医疗保健任务，是重庆市机关直属医院和干部保健医院。<br/><br/>"
									+ "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;重庆医科大学附属第一医院金山医院  是重庆市唯一一家融医疗、康复、预防、保健为一体，面向国际、国内的公立涉外医院。医院服务大众健康，以特需医疗和高端医疗服务为特色，配备现代化的诊疗设备，拥有一支精通外语、具有丰富临床经验的专家团队，实行电话预约和全程导医服务，提供一对一或多对一专家接诊，能满足不同层次患者的就诊需求。<br/><br/>"
									+ "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;重庆医科大学附属第一医院青杠老年护养中心  系国家发改委批准立项的全国首家大型公立医院下属的养老机构。中心由普通护养区、高级护养区、临湖疗养区、学术交流中心、青杠老年医院等组成，设置护养床位3000张，医院床位1000张，分五区三期建设。中心集养生、康复理疗、医疗护理、休闲娱乐等功能为一体，建成后将成为重庆市首家大型五星级综合性养老机构。<br/><br/>"
									+ "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;重庆医科大学附属第一医院大足医院  系重庆医科大学附属第一医院托管医院，是一所集医疗、教学、科研、预防保健、急救为一体的国家二级甲等综合性医院。医院能开展电视腹腔镜、胸腔镜、关节镜手术，颅内肿瘤切除术、肝叶切除术、心脏修补术、人工心脏起搏器植入术、全髋关节置换术、直肠癌根治术以及内科各种疑难病症的诊断和治疗，外科微创手术和肿瘤放射治疗水平处于重庆市区县级医院先进行列。<br/><br/>"
									+ "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;重庆医科大学附属第一医院海扶医院  系重庆医科大学附属第一医院托管医院，是集临床、科研、教学为一体的海扶无创医疗示范医院。医院以无创医疗理念为宗旨，以治疗子宫肌瘤专病为特色，多学科专家团队把国际领先的海扶超声消融治疗技术应用于临床，为患者精心设计个性化、系统化、无创微创的治疗方案，帮助患者实现“我的健康我做主”。<br/><br/>"
									+ "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;重庆医科大学附属第一医院綦江医院  系重庆医科大学附属第一医院托管医院，是集医疗、预防、保健、教学、科研、急救为一体的国家二级甲等综合医院，拥有全区规模最大、设备最好的健康体检中心。能开展晚期肝癌和肺癌的介入治疗、肿瘤的化疗和放疗、ERCP检查和治疗、颅内肿瘤治疗、微创腔镜检查治疗及各种烧伤治疗等，诊疗技术水平在全区处于领先地位，部份诊疗技术达到市级医院水平。<br/><br/>"
									+ "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;重庆医科大学附属第一医院万盛医院  系重庆医科大学附属第一医院托管医院，是集医疗、教学、科研、预防保健、急救为一体的国家二级甲等综合医院，承担万盛经开区居民和毗邻区县部分群众的医疗保健工作，以及经开区内各乡镇卫生院的教学和医疗指导任务。<br/><br/>"
									+ "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;重庆医科大学附属第一医院酉阳医院  系重庆医科大学附属第一医院托管医院，是一所集医、教、研为一体的国家二级甲等综合医院，是酉阳县医疗救治中心，担负酉阳及周边各（区）县84万人的防病救治任务。<br/><br/>"
									+ "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;重庆医科大学附属第一医院合川医院  系重庆医科大学附属第一医院托管医院，是一所集医、教、研、预防、保健、急救于一体的国家二级甲等综合医院，开展了计算机辅助下电磁导航系列手术，血液透析，高压氧治疗，肝胆、泌尿及妇科微创治疗技术，血管内、血管外介入治疗技术等，对多发伤、复合伤、多器官功能衰竭的抢救居合川区最高水平。<br/><br/>"
									+ "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;院本部地址：重庆市渝中区袁家岗友谊路1号<br/>" + "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;邮编：400016<br/>" + "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;网址：www.hospital-cqmu.com<br/>"
									+ "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;值班电话：68811360"));
		}
	}

	public void onClickBack(View view) {
		this.finish();
	}
}
