package ui;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.image.BufferedImage;
import java.util.function.Function;

import javax.swing.JComponent;
import javax.swing.SwingUtilities;

@SuppressWarnings("serial")
public class PanAndZoomImagePanel extends JComponent{

	private BufferedImage backgroundMap;
	private Dimension viewSize;
	private Rectangle viewPort; 
	private double scale = 1;

	public PanAndZoomImagePanel(){
		
		MouseAdapter mouseAdapter = new PAZMouseListener();
		this.addMouseMotionListener(mouseAdapter);
		this.addMouseListener(mouseAdapter);
		this.addMouseWheelListener(mouseAdapter);
		
		viewSize = this.getSize();
		viewPort = new Rectangle(new Point(0, 0), viewSize);
		
		this.addComponentListener( new ComponentAdapter(){
			@Override
			public void componentResized(ComponentEvent e) {
	            onResize(e);
	        }
		});
		
		
	}
	
	public void setBackgroundImage(BufferedImage image){
		backgroundMap = image;
	}
	
	public Point getGlobalPos(Point localPoint){
		Point globalPos = new Point();
		globalPos.setLocation(viewPort.x + (localPoint.getX() * scale), viewPort.y + (localPoint.getY() * scale));
		return globalPos;
	}
	public Point getLocalPos(Point globalPoint){
		Point localPos = new Point();
		localPos.setLocation((globalPoint.getX() - viewPort.x) / scale , (globalPoint.getY() - viewPort.y) / scale);
		return localPos;
	}
	public int getIntFromRGB(int r, int g, int b){
	    r = (r << 16) & 0x00FF0000;
	    g = (g << 8) & 0x0000FF00;
	    b = b & 0x000000FF;
	    return 0xFF000000 | r | g | b;
	}
	private void onResize(ComponentEvent e) {
		viewSize.setSize(this.getSize());
		zoom(0);
		repaint();
	}
	public double getScale(){
		return scale;
	}
	public void setScale(double scale) {
		this.scale = scale;
	}
	public Rectangle getViewPort(){
		return viewPort;
	}
	public void setViewPortPosition(Point newPos){
		viewPort.setLocation(newPos);
	}
	public void setPaintFunction(Function<Graphics2D, Void> paintFunction){
		secondaryPaint = paintFunction;
	}
	public void zoom(int i){
		scale += i * 0.1;
		if(scale < 0.5){
			scale = 0.5;
		}else if(scale > 5){
			scale = 5;
		}else{
			if(i != 0){
				if(i < 0){
					viewPort.translate(viewSize.width / 20, viewSize.height / 20);
				}else{
					viewPort.translate(-viewSize.width / 20, -viewSize.height / 20);
				}
			}
			viewPort.setSize((int)(viewSize.width * scale), (int)(viewSize.height * scale));
		}
	}
	
	Graphics2D g2;
	Function<Graphics2D, Void> secondaryPaint;
	
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		g2 = (Graphics2D)g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED);
		g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
		g2.drawImage(backgroundMap, 	
				0, 			0, 			viewSize.width,					viewSize.height,				// int dstx1, int dsty1, int dstx2, int dsty2,
				viewPort.x,	viewPort.y,	viewPort.x + viewPort.width,	viewPort.y + viewPort.height,	// int srcx1, int srcy1, int srcx2, int srcy2
				this);
		
		if(secondaryPaint != null){
			secondaryPaint.apply(g2);
		}
	}
	
	
	static class PAZMouseListener extends MouseAdapter {
		private final Point dragPointClicked = new Point();
		private final Point viewPortPosition = new Point();
		
		@Override 
		public void mouseDragged(MouseEvent e) {
			PanAndZoomImagePanel pazip = (PanAndZoomImagePanel)e.getSource();
			if(SwingUtilities.isMiddleMouseButton(e)){
				Point cp = e.getPoint();
				int xTrans = dragPointClicked.x - cp.x;
				int yTrans = dragPointClicked.y - cp.y;
				
				xTrans = (int)(xTrans * pazip.getScale());
				yTrans = (int)(yTrans * pazip.getScale());
				
				pazip.setViewPortPosition(new Point(viewPortPosition.x + xTrans, viewPortPosition.y + yTrans));
				
				pazip.repaint();
			}
		}
		@Override 
		public void mousePressed(MouseEvent e) {
			PanAndZoomImagePanel pazip = (PanAndZoomImagePanel)e.getSource();
			if(e.getButton() == MouseEvent.BUTTON2){
				dragPointClicked.setLocation(e.getPoint());
				viewPortPosition.setLocation(pazip.getViewPort().getLocation());
			}
		}
		
		@Override 
		public void mouseReleased(MouseEvent e) {
			PanAndZoomImagePanel pazip = (PanAndZoomImagePanel)e.getSource();
			if(e.getButton() == MouseEvent.BUTTON2){
				if(pazip.getViewPort().width + pazip.getViewPort().x < 0){
					pazip.getViewPort().x = -pazip.getViewPort().width + 100; 
				}
				if(pazip.getViewPort().height + pazip.getViewPort().y < 0){
					pazip.getViewPort().y = -pazip.getViewPort().height + 100; 
				}
				if(pazip.getViewPort().x > pazip.backgroundMap.getWidth()){
					pazip.getViewPort().x = pazip.backgroundMap.getWidth() - 100;
				}
				if(pazip.getViewPort().y > pazip.backgroundMap.getHeight()){
					pazip.getViewPort().y = pazip.backgroundMap.getHeight() - 100;
				}
				pazip.repaint();
			}
		}
		
		@Override 
		public void mouseWheelMoved(MouseWheelEvent e) {
			if(SwingUtilities.isMiddleMouseButton(e)){
				return;
			}
			if(e.getScrollType() == MouseWheelEvent.WHEEL_UNIT_SCROLL){
				PanAndZoomImagePanel pasip = (PanAndZoomImagePanel)e.getSource();
				pasip.zoom(e.getWheelRotation());
				pasip.repaint();
			}
		}
	}
}
