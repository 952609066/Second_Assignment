package lindan.cn.util;

import java.awt.Toolkit;

public class Const {
	public static final int FRAME_W = 300;
	public static final int FRAME_H = 400;
	public static final int SCREEN_W = Toolkit.getDefaultToolkit().getScreenSize().width;
	public static final int SCREEN_H = Toolkit.getDefaultToolkit().getScreenSize().height;
	
	public static final int FRAME_X = (SCREEN_W - FRAME_W)/2;
	public static final int FRAME_Y = (SCREEN_H- FRAME_H)/2;
	
	public static final String TITLE = "Calculator";
}
