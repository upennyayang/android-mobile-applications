package edu.upenn.cis.cis542;

public class Messages {
	int run;
	int abort = 0;
	int qrun;
	int id;
	double tem;
	int time;
	int fan;
	int cur_time;
	double cur_tem;
	int exist;

	public int AnalyseMgetChargetCharsg(String message) { // �õ�message�Ժ���ã�����message������message����
		if (message.charAt(0) == 'r') {
			if (message.charAt(1) == 's') {
				run = Integer.parseInt(message.substring(2));
				return 1;
			}
			if (message.charAt(1) == 'a') {
				abort = 1;
				return 2;
			}
			if (message.charAt(1) == 'q') {
				// message=message.substring(2);
				int i = message.indexOf(' ');
				// System.out.println(i);
				String temp = message.substring(2, i);
				// System.out.println(temp);
				qrun = Integer.parseInt(temp);
				message = message.substring(i + 1);
				i = message.indexOf(' ');
				temp = message.substring(0, i);
				id = Integer.parseInt(temp);
				message = message.substring(i + 1);
				i = message.indexOf(' ');
				temp = message.substring(0, i);
				tem = Double.parseDouble(temp);
				message = message.substring(i + 1);
				i = message.indexOf(' ');
				temp = message.substring(0, i);
				time = Integer.parseInt(temp);
				message = message.substring(i + 1);
				fan = Integer.parseInt(message);
				return 3;
			}

		}
		if (message.charAt(0) == 't') {
			int i = message.indexOf(' ');
			String temp = message.substring(1, i);
			cur_tem = Double.parseDouble(temp);
			message = message.substring(i + 1);
			i = message.indexOf(' ');
			temp = message.substring(0, i);
			cur_time = Integer.parseInt(temp);
			message = message.substring(i + 1);
			exist = Integer.parseInt(message);
			return 4;
		}
		return 0;
	}

	public int Getrun() { // ����rs message��runֵ
		return run;
	}

	public int Getqrun() { // ����rq message��runֵ

		return qrun;
	}

	public int Getid() { // ����rq message��id
		return id;
	}

	public int Gettime() { // ����rq message��time
		return time;
	}

	public double Gettem() { // ����rq message��temperature
		return tem;
	}

	public int Getfan() { // ����rq message��fan
		return fan;
	}

	public double Getcurtem() { // ����t message�ĵ�ǰ�¶�
		return cur_tem;
	}

	public int Getcurtime() { // ����t message�ĵ�ǰʱ��
		return cur_time;
	}

	public int Getabort() { // ����ra message���Ƿ�abort�ɹ�
		if (abort == 1) {
			abort = 0;
			return 1;
		}
		return 0;
	}

	public int Getexist() {
		return exist;
	}

	public String Setsmsg(int sid, int stemp, int stime, int sfan) { // ��ϳ�s
																		// message
		String s = "s" + sid + " " + stemp + " " + stime + " " + sfan;
		return s;
	}

	public String Setamsg() { // ��ϳ� a message
		return "a";
	}

	public String Setqmsg() { // ��ϳ�q message
		return "q";
	}

	public String Setfmsg(int ffan) { // ��ϳ�f message
		String s = "f" + ffan;
		return s;
	}

}