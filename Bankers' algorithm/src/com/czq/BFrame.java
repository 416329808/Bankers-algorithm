package com.czq;

import java.awt.Container;
import java.awt.event.*;
import java.util.*;
import java.util.regex.*;

import javax.swing.*;

public class BFrame extends JFrame {

	public JFrame Form = new JFrame("银行家");
	public Container container = Form.getContentPane();
	public JLabel jlprocess;
	public JLabel jlresources;
	public JLabel jlstate = new JLabel();
	public JLabel jlMAx =new JLabel("Max");
	public JLabel jlAll =new JLabel("Allocation"); 
	public JLabel jlNeed = new JLabel("Need");
	public JLabel jlAvi = new JLabel("Available");
	public JTextField jtprocess;

	public JTextField getJtprocess() {
		return jtprocess;
	}

	public JTextField jtresources;

	public JTextField getJtresources() {
		return jtresources;
	}

	public JComboBox<String> jComboBox = new JComboBox<String>();;
	public JButton jbrequest;
	public JButton jbCheak;
	public JButton jbreturn;
	public JButton jbCreat;
	public JTable jTable;
	public JTable avijTable;
	public JScrollPane jScrollPane;
	public JScrollPane avijScrollpane;
	public Pattern pattern = Pattern.compile("[1-9][0-9]*");// 正则表达式
	public int ResourcesNum;// 输入的资源数
	public int ProcessNum;// 输入的进程数
	public int Available[];// 系统剩余可分配资源
	public int secondAvi[];// 保存上一次系统剩余可分配资源
	public int Max[][];// 最大需求量
	public int Allocation[][];// 已分配
	public int secondall[][];
	public int Need[][];// 还需要
	public int secondned[][];
	public boolean Finish[];// 进程是否完成
	public int Work[];// 对剩余资源进行试分配
	public int Safe[];// 保存安全序列
	public int Request[];	// 资源请求数组
	public int index;	// 下拉框的下标
	public String ColumnNames[];
	public String avicolumnNames[];
	public String ObMax[][];
	public String ObAvai[][];
	public JTextField jtrequest;
	public boolean flag = true;

	public BFrame() {

		Form.setLayout(null);
		Form.setSize(760, 666);
		Form.setLocationRelativeTo(null);
		Form.setDefaultCloseOperation(3);
		Form.setResizable(false);
		Form.setDefaultCloseOperation(EXIT_ON_CLOSE);

		jlprocess = new JLabel("进程数:");
		jlprocess.setBounds(0, 50, 50, 25);
		container.add(jlprocess);
		jtprocess = new JTextField();
		jtprocess.setBounds(60, 50, 100, 25);
		container.add(jtprocess);
		// 进程输入框焦点监听事件
		jtprocess.addFocusListener(new FocusListener() {

			@Override
			public void focusLost(FocusEvent e) {
				Matcher matcherpro = pattern.matcher((CharSequence) jtprocess.getText().trim());
				if (!matcherpro.matches()) {
					jlstate.setText("请输入正整型进程数!");
				} else {
					jlstate.setText("");
					// 进程数
					ProcessNum = Integer.parseInt(jtprocess.getText().trim());
				}
			}

			@Override
			public void focusGained(FocusEvent e) {

			}
		});
		jlresources = new JLabel("资源数:");
		jlresources.setBounds(0, 95, 50, 25);
		container.add(jlresources);
		jlMAx.setBounds(300,50,50,25);
		container.add(jlMAx);
		jlAll.setBounds(400,50,80,25);
		container.add(jlAll);
		jlNeed.setBounds(520,50,50,25);
		container.add(jlNeed);
		jlAvi.setBounds(620, 50, 80, 25);
		container.add(jlAvi);
		jtresources = new JTextField();
		jtresources.setBounds(60, 95, 100, 25);
		container.add(jtresources);
		// 资源输入框焦点监听事件
		jtresources.addFocusListener(new FocusListener() {

			@Override
			public void focusLost(FocusEvent e) {
				Matcher matcherres = pattern.matcher((CharSequence) jtresources.getText().trim());
				if (!matcherres.matches()) {
					jlstate.setText("请输入正整型资源数!");
				} else {
					jlstate.setText("");
					ResourcesNum = Integer.parseInt(jtresources.getText().trim());

				}
			}

			@Override
			public void focusGained(FocusEvent e) {

			}
		});
		jbCheak = new JButton("安全检查");
		jbCheak.setBounds(50, 185, 100, 25);
		container.add(jbCheak);
		jbCheak.addActionListener(new jbCheakAction());

		jbCreat = new JButton("生成表格");
		jbCreat.setBounds(50, 140, 100, 25);
		container.add(jbCreat);
		jbCreat.addActionListener(new jbCreatAction());

		jbrequest = new JButton("申请资源");
		jbrequest.setBounds(300, 500, 100, 25);
		container.add(jbrequest);
		jbrequest.addActionListener(new jbrequestAction());

		jbreturn = new JButton("还原");
		jbreturn.setBounds(500, 500, 66, 25);
		container.add(jbreturn);
		jbreturn.addActionListener(new jbreturnAction());

		jtrequest = new JTextField();
		jtrequest.setBounds(200, 400, 100, 25);
		container.add(jtrequest);
		jlstate.setBounds(20, 300, 300, 25);
		container.add(jlstate);

		Form.setVisible(true);
	}

	// 生成按钮点击事件
	public class jbCreatAction implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			if ("".equals(jtprocess.getText()) || "".equals(jtresources.getText())) {
				jlstate.setText("请输入进程数或资源数！");
			} else {
				Matcher matcherpro = pattern.matcher((CharSequence) jtprocess.getText().trim());
				Matcher matcherres = pattern.matcher((CharSequence) jtresources.getText().trim());
				if (matcherpro.matches() && matcherres.matches()) {
					Init();

				} else {
					jlstate.setText("请输入正整型进程数或资源数！");
				}

			}
		}

	}

	// 安全检查按钮监听事件
	class jbCheakAction implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			SecurityAlgorithm();
			if (flag == true) {
				for (int i = 0; i < ProcessNum; i++) {
					jComboBox.addItem("P" + i);
				}
				jComboBox.addItemListener(new ItemListener() {

					@Override
					public void itemStateChanged(ItemEvent e) {
						if (e.getStateChange() == ItemEvent.SELECTED) {
							index = jComboBox.getSelectedIndex();
							System.out.println("索引:" + index + jComboBox.getSelectedItem());
						}
					}
				});
				jComboBox.setSelectedIndex(-1);
				jComboBox.setBounds(50, 400, 100, 25);
				container.add(jComboBox);
				flag=false;
			}

			// Form.setVisible(true);
		}

	}

	// 还原按钮监听事件
	class jbreturnAction implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			for (int i = 0; i < Available.length; i++) {
				Available[i] = secondAvi[i];
			}
			for (int i = 0; i < ProcessNum; i++) {
				for (int j = 0; j < ResourcesNum; j++) {
					Allocation[i][j] = secondall[i][j];
					Need[i][j] = secondned[i][j];
				}
			}
			table();
		}
	}

	// 请求资源按钮监听事件
	public class jbrequestAction implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {

			if ("".equals(jtrequest.getText())) {
				jlstate.setText("请输入申请资源数！");
			} else {

				Request = new int[ResourcesNum];
				String Sreq = jtrequest.getText().trim();
				String Requestsplit[] = Sreq.split(" ");
				System.out.println("申请资源:");
				for (int i = 0; i < ResourcesNum; i++) {
					try {
						// Matcher matcherreq = pattern.matcher((CharSequence)
						// Requestsplit[i]);
						// if (!matcherreq.matches()) {
						// jlstate.setText("输入资源时,请输入正整数并用空格分隔.");
						// return;
						// } else {
						Request[i] = Integer.valueOf(Requestsplit[i]);
						System.out.print(Request[i] + "\t");
						// }
					} catch (Exception e1) {
						//
						jlstate.setText("输入资源时,请输入正整数并用空格分隔.");
					}
				}

				setRequest();
			}
		}
	}

	// 对数据进行初始化
	public void Init() {

		ResourcesNum = Integer.parseInt(jtresources.getText().trim());
		// 进程数
		ProcessNum = Integer.parseInt(jtprocess.getText().trim());
		// System.out.println("资源数：" + ResourcesNum + "\t进程数:" + ProcessNum);
		ColumnNames = new String[3 * ResourcesNum+1];
		ColumnNames[0]= String.valueOf(" ");
		for (int c = 0; c < 3; c++) {
			for (int d = 0; d < ResourcesNum; d++) {
				ColumnNames[c * ResourcesNum + d+1] = String.valueOf(Character.toChars(65 + d));
			}
		}
		
		avicolumnNames = new String[ResourcesNum];
		for (int a = 0; a < ResourcesNum; a++) {
			avicolumnNames[a] = String.valueOf(Character.toChars(65 + a));

		}

		Available = new int[ResourcesNum];
		secondAvi = new int[ResourcesNum];
		ObAvai = new String[1][ResourcesNum];
		Max = new int[ProcessNum][ResourcesNum];
		ObMax = new String[ProcessNum][ResourcesNum * ResourcesNum+1];
		Allocation = new int[ProcessNum][ResourcesNum];
		secondall = new int[ProcessNum][ResourcesNum];
		Need = new int[ProcessNum][ResourcesNum];
		secondned = new int[ProcessNum][ResourcesNum];
		for (int p = 0; p < ProcessNum; p++) {
			ObMax[p][0]="P"+p;
			
		}
		for (int i = 0; i < ProcessNum; i++) {
			for (int j = 0; j < ResourcesNum; j++) {
				try {
					Max[i][j] = new Random().nextInt(10);
					ObMax[i][j+1] = String.valueOf(Max[i][j]);
					if (Max[i][j] == 0) {
						Allocation[i][j] = 0;
						ObMax[i][j + ResourcesNum+1] = String.valueOf(Allocation[i][j]);
					} else {
						Allocation[i][j] = new Random().nextInt(Max[i][j]);
						ObMax[i][j + ResourcesNum+1] = String.valueOf(Allocation[i][j]);
					}
					Need[i][j] = Max[i][j] - Allocation[i][j];
					secondall[i][j] = Allocation[i][j];
					secondned[i][j] = Need[i][j];
					ObMax[i][j + ResourcesNum * 2+1] = String.valueOf(Need[i][j]);

				} catch (Exception e) {
					System.out.println(e);
				}

				// System.out.print(Max[i][j]+"\t");
				// System.out.println("已分配："+Allocation[i][j]+"\t"+Need[i][j]);
			}
		}

		for (int k = 0; k < ResourcesNum; k++) {
			for (int p = 0; p < ProcessNum; p++) {
				Available[k] += Need[p][k];
			}
			Available[k] = Available[k] / ResourcesNum;
			secondAvi[k] = Available[k];
			try {
				ObAvai[0][k] = String.valueOf(Available[k]);
			} catch (Exception e) {
				System.out.println(e);
			}
			// System.out.println(Available[k]);
		}
		table();
	}

	// 对表格中的数据进行更新
	public void table() {
		for (int i = 0; i < ProcessNum; i++) {
			for (int j = 0; j < ResourcesNum; j++) {
				ObMax[i][j+1] = String.valueOf(Max[i][j]);
				ObMax[i][j + ResourcesNum+1] = String.valueOf(Allocation[i][j]);
				ObMax[i][j + ResourcesNum * 2+1] = String.valueOf(Need[i][j]);
				ObAvai[0][j] = String.valueOf(Available[j]);
			}

		}
		jTable = new JTable(ObMax, ColumnNames);
		jScrollPane = new JScrollPane(jTable);
		jTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		jTable.setRowHeight(25);
		// jTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

		avijTable = new JTable(ObAvai, avicolumnNames);
		avijScrollpane = new JScrollPane(avijTable);
		avijTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		avijTable.setRowHeight(25);
		// avijTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		jScrollPane.setBounds(200, 75, 400, 200);
		avijScrollpane.setBounds(600, 75, 100, 200);
		jScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		container.add(jScrollPane);
		container.add(avijScrollpane);

		Form.setVisible(true);
	}

	// 判断系统是否安全
	public void SecurityAlgorithm() {
		Finish = new boolean[ProcessNum];
		for (int fl = 0; fl < Finish.length; fl++) {
			Finish[fl] = false;

		}
		Work = new int[ResourcesNum];
		for (int wl = 0; wl < Work.length; wl++) {
			Work[wl] = Available[wl];
			// System.out.println(Work[wl]);
		}
		int count = 0;// 进程数
		int circle = 0;// 圈数
		// boolean issafe=true;
		boolean flag = true;
		Safe = new int[ProcessNum];
		while (count < ProcessNum) {
			for (int i = 0; i < ProcessNum; i++) {
				flag = true;
				for (int j = 0; j < ResourcesNum; j++) {
					if (Finish[i] == false && Need[i][j] <= Work[j]) {

					} else {
						flag = false;
						break;
					}
				}
				if (flag == true) {
					for (int j = 0; j < ResourcesNum; j++) {
						Work[j] += Allocation[i][j];
						// System.out.print(Work[j]+" ");
					}
					Finish[i] = true;
					Safe[count] = i;
					count++;
				}

			}
			circle++;
			if (count == ProcessNum) {
				String string = "系统安全状态, 此时存在一个安全序列:";
				System.out.print("此时存在一个安全序列：");
				for (int i = 0; i < Safe.length; i++) {// 输出安全序列
					System.out.print("P" + Safe[i] + "\t");
					string += "P" + Safe[i] + " ";
				}
				jlstate.setText(string);
				// System.out.println("故当前可分配！");
				break;// 跳出循环
			}
			if (count < circle) {// 判断完成进程数是否小于循环圈数
				jlstate.setText("系统进入不安全状态。");
				// System.out.println("当前系统处于不安全状态，故不存在安全序列。");
				break;// 跳出循环
			}
		}

	}

	public void setRequest() {
		boolean T = true;
		for (int j = 0; j < ResourcesNum; j++) {
			if (Request[j] <= Need[index][j]) {// 判断Request是否小于Need
				if (Request[j] <= Available[j]) {// 判断Request是否小于Available

				} else {
					jlstate.setText("当前没有足够的资源可分配，进程P" + index + "需等待。");
					System.out.println("当前没有足够的资源可分配，进程P" + index + "需等待。");
					T = false;
					return;
				}
			} else {
				jlstate.setText("进程P" + index + "请求已经超出最大需求量Need.");
				System.out.println("进程P" + index + "请求已经超出最大需求量Need.");
				T = false;
				return;
			}
		}
		if (T == true) {
			for (int i = 0; i < ResourcesNum; i++) {
				secondAvi[i] = Available[i];
				Available[i] -= Request[i];
				Allocation[index][i] += Request[i];
				Need[index][i] -= Request[i];
			}
			System.out.println("进入安全算法：");
			table();
			SecurityAlgorithm();
		}
	}

	public static void main(String[] args) {

		new BFrame();

	}
}
