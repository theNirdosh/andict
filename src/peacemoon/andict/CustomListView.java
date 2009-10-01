package peacemoon.andict;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.TextView;

public class CustomListView extends TextView {

  private Paint marginPaint;
  private Paint linePaint;
  private float margin;	
	
  /** Constructors. Each should call init() */
  public CustomListView (Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);
    init();
  }

  public CustomListView (Context context) {
    super(context);
    init();
  }

  public CustomListView (Context context, AttributeSet attrs) {
    super(context, attrs);
    init();
  }

  /** Initialize the View by creating the paint objects
   * needed for customizing onDraw 
   */
  private void init() {
	  // Get a reference to our resource table.
	  // Create the paint brushes we will use in the onDraw method.
	  marginPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
	  linePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
	
  }

  @Override
  public void onDraw(Canvas canvas) {
	  // Draw ruled lines
	  this.setTextSize(30);
	  canvas.drawLine(0, 0, getMeasuredHeight(), 0, linePaint);
	  canvas.drawLine(0, getMeasuredHeight(), getMeasuredWidth(), getMeasuredHeight(), linePaint);
	  // Draw margin
	  canvas.drawLine(margin, 0, margin, getMeasuredHeight(), marginPaint);
	  // Move the text across from the margin
	  canvas.save();
	  canvas.translate(margin, 0);
	  // Use the TextView to render the text.
	  super.onDraw(canvas);
	  canvas.restore();
	  
  }
}