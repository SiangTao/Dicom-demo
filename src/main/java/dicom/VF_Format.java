package dicom;

import java.math.BigInteger;

/**
 对VF的格式进行调整，根据不同的VR类型，进行区分
 * @author tsa
 * @time 2023-8-4
 */
public class VF_Format {

    public static final String FORMAT_STR="%02x";

    /* 根据VR的类型来选择是转换成ACSII码还是数字 */
    public enum Str{
        CS,SH,LO,ST,LT,UT,AE,PN,UI,
        DS,IS,AS
    }

    public enum Num{
        UL,US,SS,SL,AT
    }

    private boolean Stri;
    private boolean Number;
    private boolean Date;
    private boolean Time;
    private boolean DateAndTime;
    private boolean FloatingSingle; //FL
    private boolean FloatingDouble; //FD


    public VF_Format(){
        Stri=false;
        Date=false;
        Time=false;
        DateAndTime=false;
        Number=false;
        FloatingSingle=false;
        FloatingDouble=false;
    }

    public boolean IsStr(String vr){
        for(int i=0;i<Str.values().length;i++){
            if(Str.values()[i].toString().equals(vr)){return true;}
        }
        return false;
    }

    public boolean IsNum(String vr){
        for(int i=0;i<Num.values().length;i++){
            if(Num.values()[i].toString().equals(vr)){return true;}
        }
        return false;
    }

    public boolean IsDate(String vr){
        return "DA".equals(vr)?true:false;
    }

    public boolean IsTime(String vr){
        return "TM".equals(vr)?true:false;
    }

    public boolean IsDateAndTime(String vr){
        return "DT".equals(vr)?true:false;
    }

    public boolean IsFloat(String vr){ return "FL".equals(vr)?true:false; }

    public boolean IsDouble(String vr){ return "FD".equals(vr)?true:false; }


    public void VRType(String vr){
        if(IsDate(vr)){Date=true;}
        if(IsStr(vr)){Stri=true;}
        if(IsTime(vr)){Time=true;}
        if(IsDateAndTime(vr)){DateAndTime=true;}
        if(IsNum(vr)){Number=true;}
        if(IsFloat(vr)){FloatingSingle=true;}
        if(IsDouble(vr)){FloatingDouble=true;}
    }

    public String VFType(byte[] b,int location,int VLlen){
        String vf="";
        if(Stri){
            vf=StringVF(b,location,VLlen);
        }
        if(Date){
            vf=DateVF(b,location,VLlen);
        }
        if(Time){
            vf=TimeVF(b,location,VLlen);
        }
        if(DateAndTime){
            vf=DateAndTimeVF(b,location,VLlen);
        }
        if(FloatingSingle){
            vf=FloatingSingleVF(b,location,VLlen);
        }
        if(FloatingDouble){
            vf=FloatingDoubleVF(b,location,VLlen);
        }
        if(Number){
            vf=NumberVF(b,location,VLlen);
        }

        return vf;
    }



    public String StringVF(byte[] b,int location,int VLlen){

        StringBuilder VFsb=new StringBuilder();

        for(int i=0;i<VLlen;i++){
            char c=(char)(b[location++]);
            VFsb.append(c);
        }

        //StringVF最后一位有时候是空格，上传至前端会导致乱码，所以用trim函数去除
        return VFsb.toString().trim();
    }

    public String DateVF(byte[] b,int location,int VLlen){

        StringBuilder VFsb=new StringBuilder();

        for(int i=0;i<VLlen;i++){
            char c=(char)(b[location++]);
            VFsb.append(c);
            if(3==i){VFsb.append("年");}
            if(5==i){VFsb.append("月");}
            if(7==i){VFsb.append("日");}

        }
        return VFsb.toString();
    }

    public String TimeVF(byte[] b,int location,int VLlen){

        StringBuilder VFsb=new StringBuilder();

        for(int i=0;i<VLlen;i++){
            char c=(char)(b[location++]);
            VFsb.append(c);
            if(1==i){VFsb.append("点");}
            if(3==i){VFsb.append("分");}
            if(5==i){VFsb.append("秒");}
        }
        return VFsb.toString();
    }

    public String DateAndTimeVF(byte[] b,int location,int VLlen){

        StringBuilder VFsb=new StringBuilder();

        for(int i=0;i<VLlen;i++){
            char c=(char)(b[location++]);
            VFsb.append(c);
            if(3==i){VFsb.append("年");}
            if(5==i){VFsb.append("月");}
            if(7==i){VFsb.append("日");}
            if(9==i){VFsb.append("点");}
            if(11==i){VFsb.append("分");}
            if(13==i){VFsb.append("秒");}
        }
        return VFsb.toString();
    }

    public String FloatingSingleVF(byte[] b,int location,int VLlen){

        String VFs="";

        for (int i = VLlen-1; i>=0; i--) {
            VFs = VFs+String.format(FORMAT_STR, b[location + i]);
        }

        Float f = Float.intBitsToFloat(new BigInteger(VFs, 16).intValue());
        return f.toString();
    }

    public String FloatingDoubleVF(byte[] b,int location,int VLlen){

        String VFs="";

        for (int i = VLlen-1; i>=0; i--) {
            VFs = VFs+String.format(FORMAT_STR, b[location + i]);
        }
        long longHex = Long.parseUnsignedLong(VFs,16);
        double d = Double.longBitsToDouble(longHex);
        return d+"";
    }

    public String NumberVF(byte[] b,int location,int VLlen){

        String VFs="";
        int result=0;
        String s="";

        for (int i = VLlen-1; i>=0; i--) {
            VFs = VFs+String.format(FORMAT_STR, b[location + i]);
        }
        if(VLlen<8) {
            result = Integer.parseInt(VFs, 16);
            s=result+"";
        }

        if(VLlen==8) {
            for (int i = 0; i < VFs.length(); i += 4) {
                String temp = VFs.substring(i, i + 4);
                result=Integer.parseInt(temp,16);
                s=s+result+"\\";
            }
        }


        return s;
    }





}
