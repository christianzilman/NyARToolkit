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

	//NyARToolkité–¢ä¿‚
	private NyARSingleMarkerBehaviorHolder nya_behavior;

	private J3dNyARParam ar_param;

	//universeé–¢ä¿‚
	private Canvas3D canvas;

	private Locale locale;

	private VirtualUniverse universe;

	public void onUpdate(boolean i_is_marker_exist, Transform3D i_transform3d)
	{
		/*
		 * TODO:Please write your behavior operation code here.
		 * ãƒžãƒ¼ã‚«ãƒ¼ã?®å§¿å‹¢ã‚’å…ƒã?«ä»–ã?®ï¼“Dã‚ªãƒ–ã‚¸ã‚§ã‚¯ãƒˆã‚’æ“?ä½œã?™ã‚‹ã?¨ã??ã?¯ã€?ã?“ã?“ã?«å‡¦ç?†ã‚’æ›¸ã??ã?¾ã?™ã€‚*/

	}

	public void startCapture() throws Exception
	{
		// ã‚­ãƒ£ãƒ—ãƒ?ãƒ£é–‹å§‹
		nya_behavior.start();
		
		//localeã?®ä½œæˆ?ã?¨locateã?¨viewã?®è¨­å®š
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

		//è¦–ç•Œã?®è¨­å®š(ã‚«ãƒ¡ãƒ©è¨­å®šã?‹ã‚‰å?–å¾—)
		Transform3D camera_3d = ar_param.getCameraTransform();
		view.setCompatibilityModeEnable(true);
		view.setProjectionPolicy(View.PERSPECTIVE_PROJECTION);
		view.setLeftProjection(camera_3d);

		//è¦–ç‚¹è¨­å®š(0,0,0ã?‹ã‚‰ã€?Yè»¸ã‚’180åº¦å›žè»¢ã?—ã?¦Z+æ–¹å?‘ã‚’å?‘ã??ã‚ˆã?†ã?«ã?™ã‚‹ã€‚)
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

		//ãƒ?ãƒƒã‚¯ã‚°ãƒ©ã‚¦ãƒ³ãƒ‰ã?®ä½œæˆ?
		Background background = new Background();
		BoundingSphere bounds = new BoundingSphere();
		bounds.setRadius(10.0);
		background.setApplicationBounds(bounds);
		background.setImageScaleMode(Background.SCALE_FIT_ALL);
		background.setCapability(Background.ALLOW_IMAGE_WRITE);
		BranchGroup root = new BranchGroup();
		root.addChild(background);

		//TransformGroupã?§å›²ã?£ã?Ÿã‚·ãƒ¼ãƒ³ã‚°ãƒ©ãƒ•ã?®ä½œæˆ?
		TransformGroup transform = new TransformGroup();
		transform.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
		transform.addChild(createSceneGraph());
		root.addChild(transform);

		//Behaviorã?«é€£å‹•ã?™ã‚‹ã‚°ãƒ«ãƒ¼ãƒ—ã‚’ã‚»ãƒƒãƒˆ
		nya_behavior.setTransformGroup(transform);
		nya_behavior.setBackGround(background);

		//å‡ºæ?¥ã?Ÿbehaviorã‚’ã‚»ãƒƒãƒˆ
		root.addChild(nya_behavior.getBehavior());
		nya_behavior.setUpdateListener(this);

		//universe.getViewingPlatform().setNominalViewingTransform();
		
		//è¡¨ç¤ºãƒ–ãƒ©ãƒ³ãƒ?ã‚’Locateã?«ã‚»ãƒƒãƒˆ
		locale.addBranchGraph(root);
		//universe.addBranchGraph(root);

		//ã‚¦ã‚¤ãƒ³ãƒ‰ã‚¦ã?®è¨­å®š
//		setLayout(new BorderLayout());
//		add(canvas, BorderLayout.CENTER);
	}
	
	public NyARJava3DSample() throws NyARException, FileNotFoundException {
//		//NyARToolkitã?®æº–å‚™
                 InputStream i_stream = new FileInputStream(CARCODE_FILE);
		//NyARCode ar_code = NyARCode.createFromARPattFile(this.getClass().getResourceAsStream(CARCODE_FILE),16, 16);
                NyARCode ar_code = NyARCode.createFromARPattFile(i_stream,16, 16);
		//ar_param = J3dNyARParam.loadARParamFile(this.getClass().getResourceAsStream(PARAM_FILE));
                InputStream i_stream2 = new FileInputStream(PARAM_FILE);
                ar_param = J3dNyARParam.loadARParamFile(i_stream2);
		ar_param.changeScreenSize(320, 240);

		//NyARToolkitã?®Behaviorã‚’ä½œã‚‹ã€‚(ãƒžãƒ¼ã‚«ãƒ¼ã‚µã‚¤ã‚ºã?¯ãƒ¡ãƒ¼ãƒˆãƒ«ã?§æŒ‡å®šã?™ã‚‹ã?“ã?¨)
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
	 * ã‚·ãƒ¼ãƒ³ã‚°ãƒ©ãƒ•ã‚’ä½œã?£ã?¦ã€?ã??ã?®ãƒŽãƒ¼ãƒ‰ã‚’è¿”ã?™ã€‚
	 * ã?“ã?®ãƒŽãƒ¼ãƒ‰ã?¯40mmã?®è‰²ã?¤ã??ç«‹æ–¹ä½“ã‚’è¡¨ç¤ºã?™ã‚‹ã‚·ãƒ¼ãƒ³ã€‚ï½šè»¸ã‚’åŸºæº–ã?«20mmä¸Šã?«æµ®ã?‹ã?›ã?¦ã‚‹ã€‚
	 * @return
	 */
	private Node createSceneGraph()
	{
		TransformGroup tg = new TransformGroup();
		Transform3D mt = new Transform3D();
		mt.setTranslation(new Vector3d(0.00, 0.0, 20 * 0.001));
		// å¤§ã??ã?• 40mmã?®è‰²ä»˜ã??ç«‹æ–¹ä½“ã‚’ã€?Zè»¸ä¸Šã?§20mmå‹•ã?‹ã?—ã?¦é…?ç½®ï¼‰
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
