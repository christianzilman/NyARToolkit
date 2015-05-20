package jp.qualitas.nyartoolkit.java3d.webcam.sample;

import java.awt.GraphicsConfiguration;

import javax.media.j3d.Background;
import javax.media.j3d.BoundingSphere;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Canvas3D;
import javax.media.j3d.Locale;
import javax.media.j3d.Node;
import javax.media.j3d.PhysicalBody;
import javax.media.j3d.PhysicalEnvironment;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.media.j3d.View;
import javax.media.j3d.ViewPlatform;
import javax.media.j3d.VirtualUniverse;
import javax.vecmath.Vector3d;

import jp.nyatla.nyartoolkit.core.NyARCode;
import jp.nyatla.nyartoolkit.core.NyARException;
import jp.qualitas.nyartoolkit.java3d.utils.webcam.J3dNyARParam;
import jp.qualitas.nyartoolkit.java3d.utils.webcam.NyARSingleMarkerBehaviorHolder;
import jp.qualitas.nyartoolkit.java3d.utils.webcam.NyARSingleMarkerBehaviorListener;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.ds.fswebcam.FsWebcamDriver;
import com.sun.j3d.utils.geometry.ColorCube;
import com.sun.j3d.utils.universe.SimpleUniverse;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class NyARJava3DSample implements NyARSingleMarkerBehaviorListener {
    // set capture driver for fswebcam tool
	static {
		Webcam.setDriver(new FsWebcamDriver());
	}
	private final String CARCODE_FILE = "/data/patt.hiro";

	private final String PARAM_FILE = "/data/camera_para4.dat";

	//NyARToolkit関係
	private NyARSingleMarkerBehaviorHolder nya_behavior;

	private J3dNyARParam ar_param;

	//universe関係
	private Canvas3D canvas;

	private Locale locale;

	private VirtualUniverse universe;

	public void onUpdate(boolean i_is_marker_exist, Transform3D i_transform3d)
	{
		/*
		 * TODO:Please write your behavior operation code here.
		 * マーカー�?�姿勢を元�?�他�?�３Dオブジェクトを�?作�?�る�?��??�?��?�?��?��?�処�?�を書�??�?��?�。*/

	}

	public void startCapture() throws Exception
	{
		// キャプ�?ャ開始
		nya_behavior.start();
		
		//locale�?�作�?�?�locate�?�view�?�設定
		universe = new VirtualUniverse();
		//universe = new SimpleUniverse();
		locale = new Locale(universe);

		GraphicsConfiguration config = SimpleUniverse.getPreferredConfiguration();
		canvas = new Canvas3D(config);
		
		View view = new View();
		ViewPlatform viewPlatform = new ViewPlatform();
		view.attachViewPlatform(viewPlatform);
		view.addCanvas3D(canvas);
		view.setPhysicalBody(new PhysicalBody());
		view.setPhysicalEnvironment(new PhysicalEnvironment());

		//視界�?�設定(カメラ設定�?�ら�?�得)
		Transform3D camera_3d = ar_param.getCameraTransform();
		view.setCompatibilityModeEnable(true);
		view.setProjectionPolicy(View.PERSPECTIVE_PROJECTION);
		view.setLeftProjection(camera_3d);

		//視点設定(0,0,0�?�ら�?Y軸を180度回転�?��?�Z+方�?�を�?��??よ�?��?��?�る。)
		TransformGroup viewGroup = new TransformGroup();
		Transform3D viewTransform = new Transform3D();
		viewTransform.rotY(Math.PI);
		viewTransform.setTranslation(new Vector3d(0.0, 0.0, 0.0));
		viewGroup.setTransform(viewTransform);
		viewGroup.addChild(viewPlatform);
		BranchGroup viewRoot = new BranchGroup();
		viewRoot.addChild(viewGroup);
		locale.addBranchGraph(viewRoot);
		//universe.addBranchGraph(viewRoot);

		//�?ックグラウンド�?�作�?
		Background background = new Background();
		BoundingSphere bounds = new BoundingSphere();
		bounds.setRadius(10.0);
		background.setApplicationBounds(bounds);
		background.setImageScaleMode(Background.SCALE_FIT_ALL);
		background.setCapability(Background.ALLOW_IMAGE_WRITE);
		BranchGroup root = new BranchGroup();
		root.addChild(background);

		//TransformGroup�?�囲�?��?�シーングラフ�?�作�?
		TransformGroup transform = new TransformGroup();
		transform.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
		transform.addChild(createSceneGraph());
		root.addChild(transform);

		//Behavior�?�連動�?�るグループをセット
		nya_behavior.setTransformGroup(transform);
		nya_behavior.setBackGround(background);

		//出�?��?�behaviorをセット
		root.addChild(nya_behavior.getBehavior());
		nya_behavior.setUpdateListener(this);

		//universe.getViewingPlatform().setNominalViewingTransform();
		
		//表示ブラン�?をLocate�?�セット
		locale.addBranchGraph(root);
		//universe.addBranchGraph(root);

		//ウインドウ�?�設定
//		setLayout(new BorderLayout());
//		add(canvas, BorderLayout.CENTER);
	}
	
	public NyARJava3DSample() throws NyARException, FileNotFoundException {
//		//NyARToolkit�?�準備
                 InputStream i_stream = new FileInputStream(CARCODE_FILE);
		//NyARCode ar_code = NyARCode.createFromARPattFile(this.getClass().getResourceAsStream(CARCODE_FILE),16, 16);
                NyARCode ar_code = NyARCode.createFromARPattFile(i_stream,16, 16);
		//ar_param = J3dNyARParam.loadARParamFile(this.getClass().getResourceAsStream(PARAM_FILE));
                InputStream i_stream2 = new FileInputStream(PARAM_FILE);
                ar_param = J3dNyARParam.loadARParamFile(i_stream2);
		ar_param.changeScreenSize(320, 240);

		//NyARToolkit�?�Behaviorを作る。(マーカーサイズ�?�メートル�?�指定�?�る�?��?�)
		nya_behavior = new NyARSingleMarkerBehaviorHolder(ar_param, 30f, ar_code, 0.08);
		nya_behavior.setWebcapOpenListener(this);
		nya_behavior.open();
//		SimpleUniverse universe = new SimpleUniverse();
//
//		BranchGroup group = new BranchGroup();
//
//		ColorCube cube = new ColorCube(0.3);
//
//		Transform3D rotate = new Transform3D();
//
//		rotate.rotX(10);
//
//		TransformGroup rotateGroup = new TransformGroup();
//
//		rotateGroup.setTransform(rotate);
//
//		rotateGroup.addChild(cube);
//
//		group.addChild(rotateGroup);
//
//		universe.getViewingPlatform().setNominalViewingTransform();
//
//		universe.addBranchGraph(group);

	}
	/**
	 * シーングラフを作�?��?��?�??�?�ノードを返�?�。
	 * �?��?�ノード�?�40mm�?�色�?��??立方体を表示�?�るシーン。ｚ軸を基準�?�20mm上�?�浮�?��?��?�る。
	 * @return
	 */
	private Node createSceneGraph()
	{
		TransformGroup tg = new TransformGroup();
		Transform3D mt = new Transform3D();
		mt.setTranslation(new Vector3d(0.00, 0.0, 20 * 0.001));
		// 大�??�?� 40mm�?�色付�??立方体を�?Z軸上�?�20mm動�?��?��?��?置）
		tg.setTransform(mt);
		tg.addChild(new ColorCube(20 * 0.001));
		return tg;
	}

	public void onWebcamOpen() {
		try {
			this.startCapture();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	public static void main(String[] args) {

		try {
			NyARJava3DSample nyARJava3D = new NyARJava3DSample();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
