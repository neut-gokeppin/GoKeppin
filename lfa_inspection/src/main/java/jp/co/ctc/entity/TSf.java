package jp.co.ctc.entity;

/* Code Generator Information.
 * generator Version 1.0.0 release 2007/10/10
 * generated Date Wed Sep 29 13:18:51 JST 2010
 */
import java.io.Serializable;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;

/**
 * SF基本テーブル
 * @author CJ01786
 */
@Entity
public class TSf implements Serializable {

	/**
	 * Serializableインタフェースの実装に必要。
	 */
	static final long serialVersionUID = 1L;

	/**
	 * idno:bpchar(10)
	 */
	@Id
	public String idno;

	/**
	 * bdno:bpchar(5)
	 */
	public String bdno;

	/**
	 * frCode:bpchar(3)
	 */
	public String frCode;

	/**
	 * frSeq:bpchar(7)
	 */
	public String frSeq;

	/**
	 * refno:bpchar(10)
	 */
	public String refno;

	/**
	 * dumKey:bpchar(15)
	 */
	public String dumKey;

	/**
	 * vinType2:bpchar(2)
	 */
	public String vinType2;

	/**
	 * vinType:bpchar(1)
	 */
	public String vinType;

	/**
	 * vinWmi:bpchar(3)
	 */
	public String vinWmi;

	/**
	 * vinVds:bpchar(6)
	 */
	public String vinVds;

	/**
	 * vinKata:bpchar(9)
	 */
	public String vinKata;

	/**
	 * vinFr67:bpchar(1)
	 */
	public String vinFr67;

	/**
	 * vinMy:bpchar(1)
	 */
	public String vinMy;

	/**
	 * maker:bpchar(4)
	 */
	public String maker;

	/**
	 * sno:bpchar(2)
	 */
	public String sno;

	/**
	 * odrType:bpchar(1)
	 */
	public String odrType;

	/**
	 * dfsc:bpchar(5)
	 */
	public String dfsc;

	/**
	 * loDate:bpchar(8)
	 */
	@Id
	public String loDate;

	/**
	 * packMonth:bpchar(6)
	 */
	public String packMonth;

	/**
	 * carName:bpchar(2)
	 */
	public String carName;

	/**
	 * figure:bpchar(1)
	 */
	public String figure;

	/**
	 * destType:bpchar(2)
	 */
	public String destType;

	/**
	 * govAprvl:bpchar(1)
	 */
	public String govAprvl;

	/**
	 * intCode:bpchar(4)
	 */
	public String intCode;

	/**
	 * extCode:bpchar(4)
	 */
	public String extCode;

	/**
	 * prdWeek:bpchar(2)
	 */
	public String prdWeek;

	/**
	 * var1:bpchar(1)
	 */
	public String var1;

	/**
	 * destCode:bpchar(5)
	 */
	public String destCode;

	/**
	 * dest:bpchar(10)
	 */
	public String dest;

	/**
	 * psc:bpchar(1)
	 */
	public String psc;

	/**
	 * plantCode:bpchar(1)
	 */
	public String plantCode;

	/**
	 * prdctnSfx:bpchar(2)
	 */
	public String prdctnSfx;

	/**
	 * salesSfx:bpchar(2)
	 */
	public String salesSfx;

	/**
	 * importDuty:bpchar(1)
	 */
	public String importDuty;

	/**
	 * kataCode:bpchar(5)
	 */
	public String kataCode;

	/**
	 * katashiki:bpchar(20)
	 */
	public String katashiki;

	/**
	 * ctlkata:bpchar(20)
	 */
	public String ctlkata;

	/**
	 * loKatacode:bpchar(5)
	 */
	public String loKatacode;

	/**
	 * loKata:bpchar(20)
	 */
	public String loKata;

	/**
	 * engBKata:bpchar(5)
	 */
	public String engBKata;

	/**
	 * motBKata:bpchar(5)
	 */
	public String motBKata;

	/**
	 * motBKatarr:bpchar(5)
	 */
	public String motBKatarr;

	/**
	 * receiptType:bpchar(3)
	 */
	public String receiptType;

	/**
	 * kdLot:bpchar(8)
	 */
	public String kdLot;

	/**
	 * asdFrmstamp:bpchar(17)
	 */
	public String asdFrmstamp;

	/**
	 * asdVinno:bpchar(17)
	 */
	public String asdVinno;

	/**
	 * carFamily:bpchar(4)
	 */
	public String carFamily;

	/**
	 * prdreqMonth:bpchar(6)
	 */
	public String prdreqMonth;

	/**
	 * brand:bpchar(1)
	 */
	public String brand;

	/**
	 * katashiki2:bpchar(20)
	 */
	public String katashiki2;

	/**
	 * dumV:bpchar(1)
	 */
	public String dumV;

	/**
	 * spec1:bpchar(1)
	 */
	public String spec1;

	/**
	 * spec2:bpchar(1)
	 */
	public String spec2;

	/**
	 * spec3:bpchar(1)
	 */
	public String spec3;

	/**
	 * spec4:bpchar(1)
	 */
	public String spec4;

	/**
	 * spec5:bpchar(1)
	 */
	public String spec5;

	/**
	 * spec6:bpchar(1)
	 */
	public String spec6;

	/**
	 * spec7:bpchar(1)
	 */
	public String spec7;

	/**
	 * spec8:bpchar(1)
	 */
	public String spec8;

	/**
	 * spec9:bpchar(1)
	 */
	public String spec9;

	/**
	 * spec10:bpchar(1)
	 */
	public String spec10;

	/**
	 * spec11:bpchar(1)
	 */
	public String spec11;

	/**
	 * spec12:bpchar(1)
	 */
	public String spec12;

	/**
	 * spec13:bpchar(1)
	 */
	public String spec13;

	/**
	 * spec14:bpchar(1)
	 */
	public String spec14;

	/**
	 * spec15:bpchar(1)
	 */
	public String spec15;

	/**
	 * spec16:bpchar(1)
	 */
	public String spec16;

	/**
	 * spec17:bpchar(1)
	 */
	public String spec17;

	/**
	 * spec18:bpchar(1)
	 */
	public String spec18;

	/**
	 * spec19:bpchar(1)
	 */
	public String spec19;

	/**
	 * spec20:bpchar(1)
	 */
	public String spec20;

	/**
	 * spec21:bpchar(1)
	 */
	public String spec21;

	/**
	 * spec22:bpchar(1)
	 */
	public String spec22;

	/**
	 * spec23:bpchar(1)
	 */
	public String spec23;

	/**
	 * spec24:bpchar(1)
	 */
	public String spec24;

	/**
	 * spec25:bpchar(1)
	 */
	public String spec25;

	/**
	 * spec26:bpchar(1)
	 */
	public String spec26;

	/**
	 * spec27:bpchar(1)
	 */
	public String spec27;

	/**
	 * spec28:bpchar(1)
	 */
	public String spec28;

	/**
	 * spec29:bpchar(1)
	 */
	public String spec29;

	/**
	 * spec30:bpchar(1)
	 */
	public String spec30;

	/**
	 * spec31:bpchar(1)
	 */
	public String spec31;

	/**
	 * spec32:bpchar(1)
	 */
	public String spec32;

	/**
	 * spec33:bpchar(1)
	 */
	public String spec33;

	/**
	 * spec34:bpchar(1)
	 */
	public String spec34;

	/**
	 * spec35:bpchar(1)
	 */
	public String spec35;

	/**
	 * spec36:bpchar(1)
	 */
	public String spec36;

	/**
	 * spec37:bpchar(1)
	 */
	public String spec37;

	/**
	 * spec38:bpchar(1)
	 */
	public String spec38;

	/**
	 * spec39:bpchar(1)
	 */
	public String spec39;

	/**
	 * spec40:bpchar(1)
	 */
	public String spec40;

	/**
	 * spec41:bpchar(1)
	 */
	public String spec41;

	/**
	 * spec42:bpchar(1)
	 */
	public String spec42;

	/**
	 * spec43:bpchar(1)
	 */
	public String spec43;

	/**
	 * spec44:bpchar(1)
	 */
	public String spec44;

	/**
	 * spec45:bpchar(1)
	 */
	public String spec45;

	/**
	 * spec46:bpchar(1)
	 */
	public String spec46;

	/**
	 * spec47:bpchar(1)
	 */
	public String spec47;

	/**
	 * spec48:bpchar(1)
	 */
	public String spec48;

	/**
	 * spec49:bpchar(1)
	 */
	public String spec49;

	/**
	 * spec50:bpchar(1)
	 */
	public String spec50;

	/**
	 * spec51:bpchar(1)
	 */
	public String spec51;

	/**
	 * spec52:bpchar(1)
	 */
	public String spec52;

	/**
	 * spec53:bpchar(1)
	 */
	public String spec53;

	/**
	 * spec54:bpchar(1)
	 */
	public String spec54;

	/**
	 * spec55:bpchar(1)
	 */
	public String spec55;

	/**
	 * spec56:bpchar(1)
	 */
	public String spec56;

	/**
	 * spec57:bpchar(1)
	 */
	public String spec57;

	/**
	 * spec58:bpchar(1)
	 */
	public String spec58;

	/**
	 * spec59:bpchar(1)
	 */
	public String spec59;

	/**
	 * spec60:bpchar(1)
	 */
	public String spec60;

	/**
	 * spec61:bpchar(1)
	 */
	public String spec61;

	/**
	 * spec62:bpchar(1)
	 */
	public String spec62;

	/**
	 * spec63:bpchar(1)
	 */
	public String spec63;

	/**
	 * spec64:bpchar(1)
	 */
	public String spec64;

	/**
	 * spec65:bpchar(1)
	 */
	public String spec65;

	/**
	 * spec66:bpchar(1)
	 */
	public String spec66;

	/**
	 * spec67:bpchar(1)
	 */
	public String spec67;

	/**
	 * spec68:bpchar(1)
	 */
	public String spec68;

	/**
	 * spec69:bpchar(1)
	 */
	public String spec69;

	/**
	 * spec70:bpchar(1)
	 */
	public String spec70;

	/**
	 * spec71:bpchar(1)
	 */
	public String spec71;

	/**
	 * spec72:bpchar(1)
	 */
	public String spec72;

	/**
	 * spec73:bpchar(1)
	 */
	public String spec73;

	/**
	 * spec74:bpchar(1)
	 */
	public String spec74;

	/**
	 * spec75:bpchar(1)
	 */
	public String spec75;

	/**
	 * spec76:bpchar(1)
	 */
	public String spec76;

	/**
	 * spec77:bpchar(1)
	 */
	public String spec77;

	/**
	 * spec78:bpchar(1)
	 */
	public String spec78;

	/**
	 * spec79:bpchar(1)
	 */
	public String spec79;

	/**
	 * spec80:bpchar(1)
	 */
	public String spec80;

	/**
	 * spec81:bpchar(1)
	 */
	public String spec81;

	/**
	 * spec82:bpchar(1)
	 */
	public String spec82;

	/**
	 * spec83:bpchar(1)
	 */
	public String spec83;

	/**
	 * spec84:bpchar(1)
	 */
	public String spec84;

	/**
	 * spec85:bpchar(1)
	 */
	public String spec85;

	/**
	 * spec86:bpchar(1)
	 */
	public String spec86;

	/**
	 * spec87:bpchar(1)
	 */
	public String spec87;

	/**
	 * spec88:bpchar(1)
	 */
	public String spec88;

	/**
	 * spec89:bpchar(1)
	 */
	public String spec89;

	/**
	 * spec90:bpchar(1)
	 */
	public String spec90;

	/**
	 * spec91:bpchar(1)
	 */
	public String spec91;

	/**
	 * spec92:bpchar(1)
	 */
	public String spec92;

	/**
	 * spec93:bpchar(1)
	 */
	public String spec93;

	/**
	 * spec94:bpchar(1)
	 */
	public String spec94;

	/**
	 * spec95:bpchar(1)
	 */
	public String spec95;

	/**
	 * spec96:bpchar(1)
	 */
	public String spec96;

	/**
	 * spec97:bpchar(1)
	 */
	public String spec97;

	/**
	 * spec98:bpchar(1)
	 */
	public String spec98;

	/**
	 * spec99:bpchar(1)
	 */
	public String spec99;

	/**
	 * spec100:bpchar(1)
	 */
	public String spec100;

	/**
	 * spec101:bpchar(1)
	 */
	public String spec101;

	/**
	 * spec102:bpchar(1)
	 */
	public String spec102;

	/**
	 * spec103:bpchar(1)
	 */
	public String spec103;

	/**
	 * spec104:bpchar(1)
	 */
	public String spec104;

	/**
	 * spec105:bpchar(1)
	 */
	public String spec105;

	/**
	 * spec106:bpchar(1)
	 */
	public String spec106;

	/**
	 * spec107:bpchar(1)
	 */
	public String spec107;

	/**
	 * spec108:bpchar(1)
	 */
	public String spec108;

	/**
	 * spec109:bpchar(1)
	 */
	public String spec109;

	/**
	 * spec110:bpchar(1)
	 */
	public String spec110;

	/**
	 * spec111:bpchar(1)
	 */
	public String spec111;

	/**
	 * spec112:bpchar(1)
	 */
	public String spec112;

	/**
	 * spec113:bpchar(1)
	 */
	public String spec113;

	/**
	 * spec114:bpchar(1)
	 */
	public String spec114;

	/**
	 * spec115:bpchar(1)
	 */
	public String spec115;

	/**
	 * spec116:bpchar(1)
	 */
	public String spec116;

	/**
	 * spec117:bpchar(1)
	 */
	public String spec117;

	/**
	 * spec118:bpchar(1)
	 */
	public String spec118;

	/**
	 * spec119:bpchar(1)
	 */
	public String spec119;

	/**
	 * spec120:bpchar(1)
	 */
	public String spec120;

	/**
	 * spec121:bpchar(1)
	 */
	public String spec121;

	/**
	 * spec122:bpchar(1)
	 */
	public String spec122;

	/**
	 * spec123:bpchar(1)
	 */
	public String spec123;

	/**
	 * spec124:bpchar(1)
	 */
	public String spec124;

	/**
	 * spec125:bpchar(1)
	 */
	public String spec125;

	/**
	 * spec126:bpchar(1)
	 */
	public String spec126;

	/**
	 * spec127:bpchar(1)
	 */
	public String spec127;

	/**
	 * spec128:bpchar(1)
	 */
	public String spec128;

	/**
	 * spec129:bpchar(1)
	 */
	public String spec129;

	/**
	 * spec130:bpchar(1)
	 */
	public String spec130;

	/**
	 * spec131:bpchar(1)
	 */
	public String spec131;

	/**
	 * spec132:bpchar(1)
	 */
	public String spec132;

	/**
	 * spec133:bpchar(1)
	 */
	public String spec133;

	/**
	 * spec134:bpchar(1)
	 */
	public String spec134;

	/**
	 * spec135:bpchar(1)
	 */
	public String spec135;

	/**
	 * spec136:bpchar(1)
	 */
	public String spec136;

	/**
	 * spec137:bpchar(1)
	 */
	public String spec137;

	/**
	 * spec138:bpchar(1)
	 */
	public String spec138;

	/**
	 * spec139:bpchar(1)
	 */
	public String spec139;

	/**
	 * spec140:bpchar(1)
	 */
	public String spec140;

	/**
	 * spec141:bpchar(1)
	 */
	public String spec141;

	/**
	 * spec142:bpchar(1)
	 */
	public String spec142;

	/**
	 * spec143:bpchar(1)
	 */
	public String spec143;

	/**
	 * spec144:bpchar(1)
	 */
	public String spec144;

	/**
	 * spec145:bpchar(1)
	 */
	public String spec145;

	/**
	 * spec146:bpchar(1)
	 */
	public String spec146;

	/**
	 * spec147:bpchar(1)
	 */
	public String spec147;

	/**
	 * spec148:bpchar(1)
	 */
	public String spec148;

	/**
	 * spec149:bpchar(1)
	 */
	public String spec149;

	/**
	 * spec150:bpchar(1)
	 */
	public String spec150;

	/**
	 * spec151:bpchar(1)
	 */
	public String spec151;

	/**
	 * spec152:bpchar(1)
	 */
	public String spec152;

	/**
	 * spec153:bpchar(1)
	 */
	public String spec153;

	/**
	 * spec154:bpchar(1)
	 */
	public String spec154;

	/**
	 * spec155:bpchar(1)
	 */
	public String spec155;

	/**
	 * spec156:bpchar(1)
	 */
	public String spec156;

	/**
	 * spec157:bpchar(1)
	 */
	public String spec157;

	/**
	 * spec158:bpchar(1)
	 */
	public String spec158;

	/**
	 * spec159:bpchar(1)
	 */
	public String spec159;

	/**
	 * spec160:bpchar(1)
	 */
	public String spec160;

	/**
	 * spec161:bpchar(1)
	 */
	public String spec161;

	/**
	 * spec162:bpchar(1)
	 */
	public String spec162;

	/**
	 * spec163:bpchar(1)
	 */
	public String spec163;

	/**
	 * spec164:bpchar(1)
	 */
	public String spec164;

	/**
	 * spec165:bpchar(1)
	 */
	public String spec165;

	/**
	 * spec166:bpchar(1)
	 */
	public String spec166;

	/**
	 * spec167:bpchar(1)
	 */
	public String spec167;

	/**
	 * spec168:bpchar(1)
	 */
	public String spec168;

	/**
	 * spec169:bpchar(1)
	 */
	public String spec169;

	/**
	 * spec170:bpchar(1)
	 */
	public String spec170;

	/**
	 * spec171:bpchar(1)
	 */
	public String spec171;

	/**
	 * spec172:bpchar(1)
	 */
	public String spec172;

	/**
	 * spec173:bpchar(1)
	 */
	public String spec173;

	/**
	 * spec174:bpchar(1)
	 */
	public String spec174;

	/**
	 * spec175:bpchar(1)
	 */
	public String spec175;

	/**
	 * spec176:bpchar(1)
	 */
	public String spec176;

	/**
	 * spec177:bpchar(1)
	 */
	public String spec177;

	/**
	 * spec178:bpchar(1)
	 */
	public String spec178;

	/**
	 * spec179:bpchar(1)
	 */
	public String spec179;

	/**
	 * spec180:bpchar(1)
	 */
	public String spec180;

	/**
	 * spec181:bpchar(1)
	 */
	public String spec181;

	/**
	 * spec182:bpchar(1)
	 */
	public String spec182;

	/**
	 * spec183:bpchar(1)
	 */
	public String spec183;

	/**
	 * spec184:bpchar(1)
	 */
	public String spec184;

	/**
	 * spec185:bpchar(1)
	 */
	public String spec185;

	/**
	 * spec186:bpchar(1)
	 */
	public String spec186;

	/**
	 * spec187:bpchar(1)
	 */
	public String spec187;

	/**
	 * spec188:bpchar(1)
	 */
	public String spec188;

	/**
	 * spec189:bpchar(1)
	 */
	public String spec189;

	/**
	 * spec190:bpchar(1)
	 */
	public String spec190;

	/**
	 * spec191:bpchar(1)
	 */
	public String spec191;

	/**
	 * spec192:bpchar(1)
	 */
	public String spec192;

	/**
	 * spec193:bpchar(1)
	 */
	public String spec193;

	/**
	 * spec194:bpchar(1)
	 */
	public String spec194;

	/**
	 * spec195:bpchar(1)
	 */
	public String spec195;

	/**
	 * spec196:bpchar(1)
	 */
	public String spec196;

	/**
	 * spec197:bpchar(1)
	 */
	public String spec197;

	/**
	 * spec198:bpchar(1)
	 */
	public String spec198;

	/**
	 * spec199:bpchar(1)
	 */
	public String spec199;

	/**
	 * spec200:bpchar(1)
	 */
	public String spec200;

	/**
	 * specType:bpchar(1)
	 */
	public String specType;

	/**
	 * dumSpec:bpchar(99)
	 */
	public String dumSpec;

	/**
	 * tecsOdrno:bpchar(12)
	 */
	public String tecsOdrno;

	/**
	 * tecsKata:bpchar(25)
	 */
	public String tecsKata;

	/**
	 * tecsMaker:bpchar(5)
	 */
	public String tecsMaker;

	/**
	 * tecsDealer:bpchar(5)
	 */
	public String tecsDealer;

	/**
	 * equipmentLine:bpchar(2)
	 */
	public String equipmentLine;

	/**
	 * dumVsect1:bpchar(1)
	 */
	public String dumVsect1;

	/**
	 * dumVsect2:bpchar(50)
	 */
	public String dumVsect2;

	/**
	 * bdline:bpchar(1)
	 */
	public String bdline;

	/**
	 * procCode:bpchar(2)
	 */
	public String procCode;

	/**
	 * bctype:bpchar(1)
	 */
	public String bctype;

	/**
	 * theftLbl:bpchar(1)
	 */
	public String theftLbl;

	/**
	 * subFrcode:bpchar(1)
	 */
	public String subFrcode;

	/**
	 * subBdcode:bpchar(1)
	 */
	public String subBdcode;

	/**
	 * nofrnoFlg:bpchar(1)
	 */
	public String nofrnoFlg;

	/**
	 * vehclType:bpchar(1)
	 */
	public String vehclType;

	/**
	 * frLine:bpchar(1)
	 */
	public String frLine;

	/**
	 * splfrType:bpchar(1)
	 */
	public String splfrType;

	/**
	 * finLine:bpchar(1)
	 */
	public String finLine;

	/**
	 * finTp:bpchar(2)
	 */
	public String finTp;

	/**
	 * copyFlg:bpchar(1)
	 */
	public String copyFlg;

	/**
	 * dollylocks:bpchar(20)
	 */
	public String dollylocks;

	/**
	 * dollyCondit:bpchar(1)
	 */
	public String dollyCondit;

	/**
	 * chassisLine:bpchar(1)
	 */
	public String chassisLine;

	/**
	 * batBdline:bpchar(1)
	 */
	public String batBdline;

	/**
	 * wCtrlS:bpchar(1)
	 */
	public String wCtrlS;

	/**
	 * astBdodrS:bpchar(1)
	 */
	public String astBdodrS;

	/**
	 * vinEditorS:bpchar(1)
	 */
	public String vinEditorS;

	/**
	 * dumAlc:bpchar(59)
	 */
	public String dumAlc;

	/**
	 * asmline:bpchar(1)
	 */
	public String asmline;

	/**
	 * paintline:bpchar(1)
	 */
	public String paintline;

	/**
	 * fbltype:bpchar(1)
	 */
	public String fbltype;

	/**
	 * prgMngflg:bpchar(1)
	 */
	public String prgMngflg;

	/**
	 * gateptn:bpchar(1)
	 */
	public String gateptn;

	/**
	 * gateno:bpchar(2)
	 */
	public String gateno;

	/**
	 * convrtFlg:bpchar(3)
	 */
	public String convrtFlg;

	/**
	 * schDate:bpchar(8)
	 */
	public String schDate;

	/**
	 * linename:bpchar(1)
	 */
	public String linename;

	/**
	 * clrLot:bpchar(2)
	 */
	public String clrLot;

	/**
	 * seqno:bpchar(5)
	 */
	public String seqno;

	/**
	 * seqlistno:bpchar(5)
	 */
	public String seqlistno;

	/**
	 * sepano:bpchar(7)
	 */
	public String sepano;

	/**
	 * tmpBdseqno:bpchar(6)
	 */
	public String tmpBdseqno;

	/**
	 * tmpBdseq:bpchar(7)
	 */
	public String tmpBdseq;

	/**
	 * tmpAsmseq:bpchar(7)
	 */
	public String tmpAsmseq;

	/**
	 * fixSeqno:bpchar(6)
	 */
	public String fixSeqno;

	/**
	 * fixBdseq:bpchar(7)
	 */
	public String fixBdseq;

	/**
	 * fixPaintseq:bpchar(7)
	 */
	public String fixPaintseq;

	/**
	 * fixAsmseq:bpchar(7)
	 */
	public String fixAsmseq;

	/**
	 * seqnoDate:bpchar(8)
	 */
	public String seqnoDate;

	/**
	 * procQty:bpchar(4)
	 */
	public String procQty;

	/**
	 * asmseqDate:bpchar(8)
	 */
	public String asmseqDate;

	/**
	 * tecsInfo:bpchar(15)
	 */
	public String tecsInfo;

	/**
	 * tmpChseqno:bpchar(6)
	 */
	public String tmpChseqno;

	/**
	 * fixChseqno:bpchar(6)
	 */
	public String fixChseqno;

	/**
	 * fixcChseqno:bpchar(7)
	 */
	public String fixcChseqno;

	/**
	 * dumSch1:bpchar(1)
	 */
	public String dumSch1;

	/**
	 * delivrRegn:bpchar(3)
	 */
	public String delivrRegn;

	/**
	 * dlrOdrno:bpchar(17)
	 */
	public String dlrOdrno;

	/**
	 * custFlg:bpchar(2)
	 */
	public String custFlg;

	/**
	 * allocFlg:bpchar(2)
	 */
	public String allocFlg;

	/**
	 * custInf:bpchar(2)
	 */
	public String custInf;

	/**
	 * dlrName:bpchar(17)
	 */
	public String dlrName;

	/**
	 * wrap:bpchar(3)
	 */
	public String wrap;

	/**
	 * wrapFlg:bpchar(1)
	 */
	public String wrapFlg;

	/**
	 * carryinDest:bpchar(8)
	 */
	public String carryinDest;

	/**
	 * dumCust:bpchar(5)
	 */
	public String dumCust;

	/**
	 * schSndflg:bpchar(1)
	 */
	public String schSndflg;

	/**
	 * tempSeqflg:bpchar(1)
	 */
	public String tempSeqflg;

	/**
	 * fixSeqflg:bpchar(1)
	 */
	public String fixSeqflg;

	/**
	 * rejFlg:bpchar(1)
	 */
	public String rejFlg;

	/**
	 * carryfwdFlg:bpchar(1)
	 */
	public String carryfwdFlg;

	/**
	 * seqflLn:bpchar(1)
	 */
	public String seqflLn;

	/**
	 * ltaddCode:bpchar(10)
	 */
	public String ltaddCode;

	/**
	 * chasRejFlg:bpchar(1)
	 */
	public String chasRejFlg;

	/**
	 * bdResndFlg:bpchar(1)
	 */
	public String bdResndFlg;

	/**
	 * chResndFlg:bpchar(1)
	 */
	public String chResndFlg;

	/**
	 * dumHost:bpchar(31)
	 */
	public String dumHost;

	/**
	 * intDeliv:bpchar(3)
	 */
	public String intDeliv;

	/**
	 * ecas:bpchar(16)
	 */
	public String ecas;

	/**
	 * dumSys:bpchar(31)
	 */
	public String dumSys;

	/**
	 * approval:bpchar(21)
	 */
	public String approval;

	/**
	 * maxweight:bpchar(5)
	 */
	public String maxweight;

	/**
	 * maxconbi:bpchar(5)
	 */
	public String maxconbi;

	/**
	 * frAxleload:bpchar(4)
	 */
	public String frAxleload;

	/**
	 * rrAxleload:bpchar(4)
	 */
	public String rrAxleload;

	/**
	 * frTiresize:bpchar(14)
	 */
	public String frTiresize;

	/**
	 * rrTiresize:bpchar(14)
	 */
	public String rrTiresize;

	/**
	 * frRimsize:bpchar(11)
	 */
	public String frRimsize;

	/**
	 * rrRimsize:bpchar(11)
	 */
	public String rrRimsize;

	/**
	 * frTireprsr:bpchar(2)
	 */
	public String frTireprsr;

	/**
	 * rrTireprsr:bpchar(2)
	 */
	public String rrTireprsr;

	/**
	 * trans:bpchar(6)
	 */
	public String trans;

	/**
	 * axle:bpchar(4)
	 */
	public String axle;

	/**
	 * engineKata:bpchar(8)
	 */
	public String engineKata;

	/**
	 * engineDisp:bpchar(4)
	 */
	public String engineDisp;

	/**
	 * category:bpchar(8)
	 */
	public String category;

	/**
	 * printno:bpchar(2)
	 */
	public String printno;

	/**
	 * maxweight2:bpchar(5)
	 */
	public String maxweight2;

	/**
	 * maxconbi2:bpchar(5)
	 */
	public String maxconbi2;

	/**
	 * frAxleload2:bpchar(4)
	 */
	public String frAxleload2;

	/**
	 * rrAxleload2:bpchar(4)
	 */
	public String rrAxleload2;

	/**
	 * frTireprsr2:bpchar(3)
	 */
	public String frTireprsr2;

	/**
	 * rrTireprsr2:bpchar(3)
	 */
	public String rrTireprsr2;

	/**
	 * plateKata:bpchar(22)
	 */
	public String plateKata;

	/**
	 * emitnCode:bpchar(5)
	 */
	public String emitnCode;

	/**
	 * dumCr1:bpchar(74)
	 */
	public String dumCr1;

	/**
	 * dumCr2:bpchar(50)
	 */
	public String dumCr2;

	/**
	 * lastline:bpchar(1)
	 */
	public String lastline;

	/**
	 * lasttp:bpchar(2)
	 */
	public String lasttp;

	/**
	 * lastbcno:bpchar(3)
	 */
	public String lastbcno;

	/**
	 * lasttimes:bpchar(1)
	 */
	public String lasttimes;

	/**
	 * lastpdate:bpchar(13)
	 */
	public String lastpdate;

	/**
	 * lastdate:bpchar(12)
	 */
	public String lastdate;

	/**
	 * present:bpchar(2)
	 */
	public String present;

	/**
	 * cdlytime:bpchar(7)
	 */
	public String cdlytime;

	/**
	 * prgrsSgn:bpchar(1)
	 */
	public String prgrsSgn;

	/**
	 * csprTp:bpchar(7)
	 */
	public String csprTp;

	/**
	 * csprByo:bpchar(7)
	 */
	public String csprByo;

	/**
	 * csprDeli:bpchar(7)
	 */
	public String csprDeli;

	/**
	 * ltresult:bpchar(7)
	 */
	public String ltresult;

	/**
	 * ltresultBs:bpchar(7)
	 */
	public String ltresultBs;

	/**
	 * presentline:bpchar(1)
	 */
	public String presentline;

	/**
	 * dumPresent:bpchar(12)
	 */
	public String dumPresent;

	/**
	 * repline:bpchar(1)
	 */
	public String repline;

	/**
	 * reppoint:bpchar(2)
	 */
	public String reppoint;

	/**
	 * onlineflg:bpchar(1)
	 */
	public String onlineflg;

	/**
	 * refline:bpchar(1)
	 */
	public String refline;

	/**
	 * reftp:bpchar(2)
	 */
	public String reftp;

	/**
	 * dumRepsts:bpchar(3)
	 */
	public String dumRepsts;

	/**
	 * frmstamp:bpchar(17)
	 */
	public String frmstamp;

	/**
	 * vinno:bpchar(17)
	 */
	public String vinno;

	/**
	 * vincd:bpchar(1)
	 */
	public String vincd;

	/**
	 * dumVin:bpchar(11)
	 */
	public String dumVin;

	/**
	 * immobikey:bpchar(3)
	 */
	public String immobikey;

	/**
	 * keyno:bpchar(6)
	 */
	public String keyno;

	/**
	 * egno:bpchar(7)
	 */
	public String egno;

	/**
	 * tiremaker:bpchar(2)
	 */
	public String tiremaker;

	/**
	 * airbagD:bpchar(20)
	 */
	public String airbagD;

	/**
	 * airbagP:bpchar(20)
	 */
	public String airbagP;

	/**
	 * airbagFrs:bpchar(20)
	 */
	public String airbagFrs;

	/**
	 * airbagFls:bpchar(20)
	 */
	public String airbagFls;

	/**
	 * airbagRrs:bpchar(20)
	 */
	public String airbagRrs;

	/**
	 * airbagRls:bpchar(20)
	 */
	public String airbagRls;

	/**
	 * airbagFrc:bpchar(20)
	 */
	public String airbagFrc;

	/**
	 * airbagFlc:bpchar(20)
	 */
	public String airbagFlc;

	/**
	 * airbagRrc:bpchar(20)
	 */
	public String airbagRrc;

	/**
	 * airbagRlc:bpchar(20)
	 */
	public String airbagRlc;

	/**
	 * airbagFrk:bpchar(20)
	 */
	public String airbagFrk;

	/**
	 * airbagFlk:bpchar(20)
	 */
	public String airbagFlk;

	/**
	 * airbag_3rc:bpchar(20)
	 */
	@Column(name = "airbag_3rc")
	public String airbag3rc;

	/**
	 * airbag_3lc:bpchar(20)
	 */
	@Column(name = "airbag_3lc")
	public String airbag3lc;

	/**
	 * airbagEcu:bpchar(20)
	 */
	public String airbagEcu;

	/**
	 * cvtno:bpchar(20)
	 */
	public String cvtno;

	/**
	 * diagChk:bpchar(1)
	 */
	public String diagChk;

	/**
	 * deckpartno:bpchar(10)
	 */
	public String deckpartno;

	/**
	 * framepartno:bpchar(10)
	 */
	public String framepartno;

	/**
	 * crlabelres:bpchar(18)
	 */
	public String crlabelres;

	/**
	 * finalBuyoff:bpchar(1)
	 */
	public String finalBuyoff;

	/**
	 * airbagHood:bpchar(20)
	 */
	public String airbagHood;

	/**
	 * airbagResv6:bpchar(20)
	 */
	public String airbagResv6;

	/**
	 * paintedcode:bpchar(4)
	 */
	public String paintedcode;

	/**
	 * dumNum:bpchar(32)
	 */
	public String dumNum;

	/**
	 * dumTemp:bpchar(3)
	 */
	public String dumTemp;

	/**
	 * inspectSign:bpchar(1)
	 */
	public String inspectSign;

	/**
	 * accident:bpchar(1)
	 */
	public String accident;

	/**
	 * holdLine:bpchar(1)
	 */
	public String holdLine;

	/**
	 * holdTp:bpchar(2)
	 */
	public String holdTp;

	/**
	 * holdCode1:bpchar(2)
	 */
	public String holdCode1;

	/**
	 * holdCode2:bpchar(2)
	 */
	public String holdCode2;

	/**
	 * holdCode3:bpchar(2)
	 */
	public String holdCode3;

	/**
	 * holdPtime:bpchar(13)
	 */
	public String holdPtime;

	/**
	 * delbaseDate:bpchar(8)
	 */
	public String delbaseDate;

	/**
	 * eraseTime:bpchar(14)
	 */
	public String eraseTime;

	/**
	 * excTime:bpchar(14)
	 */
	public String excTime;

	/**
	 * amendTime:bpchar(14)
	 */
	public String amendTime;

	/**
	 * amendDfscTime:bpchar(14)
	 */
	public String amendDfscTime;

	/**
	 * amendEntryTime:bpchar(14)
	 */
	public String amendEntryTime;

	/**
	 * cancelTime:bpchar(14)
	 */
	public String cancelTime;

	/**
	 * jt5Month:bpchar(6)
	 */
	public String jt5Month;

	/**
	 * stsChangeTime:bpchar(14)
	 */
	public String stsChangeTime;

	/**
	 * inptTrm:bpchar(1)
	 */
	public String inptTrm;

	/**
	 * egchangeFlg:bpchar(1)
	 */
	public String egchangeFlg;

	/**
	 * newmodelFlg:bpchar(1)
	 */
	public String newmodelFlg;

	/**
	 * spvehicleFlg:bpchar(1)
	 */
	public String spvehicleFlg;

	/**
	 * auditCode01:bpchar(3)
	 */
	public String auditCode01;

	/**
	 * auditCode02:bpchar(3)
	 */
	public String auditCode02;

	/**
	 * auditCode03:bpchar(3)
	 */
	public String auditCode03;

	/**
	 * auditCode04:bpchar(3)
	 */
	public String auditCode04;

	/**
	 * auditCode05:bpchar(3)
	 */
	public String auditCode05;

	/**
	 * auditCode06:bpchar(3)
	 */
	public String auditCode06;

	/**
	 * auditCode07:bpchar(3)
	 */
	public String auditCode07;

	/**
	 * auditCode08:bpchar(3)
	 */
	public String auditCode08;

	/**
	 * auditCode09:bpchar(3)
	 */
	public String auditCode09;

	/**
	 * auditCode10:bpchar(3)
	 */
	public String auditCode10;

	/**
	 * apvPrNo:bpchar(12)
	 */
	public String apvPrNo;

	/**
	 * dumStat1:bpchar(15)
	 */
	public String dumStat1;

	/**
	 * gbkDcmid:bpchar(20)
	 */
	public String gbkDcmid;

	/**
	 * gbkTrmid:bpchar(20)
	 */
	public String gbkTrmid;

	/**
	 * prdRegNo:bpchar(9)
	 */
	public String prdRegNo;

	/**
	 * satradioId:bpchar(20)
	 */
	public String satradioId;

	/**
	 * satradioSn:bpchar(20)
	 */
	public String satradioSn;

	/**
	 * dumStat2:bpchar(11)
	 */
	public String dumStat2;

	/**
	 * freeResult1:bpchar(50)
	 */
	public String freeResult1;

	/**
	 * freeResult2:bpchar(50)
	 */
	public String freeResult2;

	/**
	 * comment1:bpchar(50)
	 */
	public String comment1;

	/**
	 * comment2:bpchar(50)
	 */
	public String comment2;

	/**
	 * comment3:bpchar(50)
	 */
	public String comment3;

	/**
	 * comment4:bpchar(50)
	 */
	public String comment4;

	/**
	 * 関連エンティティ：BCデータ
	 */
	@OneToMany (mappedBy = "tSf")
	public List<FBcdata> fBcdataList;
}
