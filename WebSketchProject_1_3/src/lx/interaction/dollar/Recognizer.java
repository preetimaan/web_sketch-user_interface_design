package lx.interaction.dollar;

import java.util.*;

public class Recognizer
{
	//
	// Recognizer class constants
	//
	int NumTemplates = 16;
	public static int NumPoints = 64;
	public static double SquareSize = 250.0;
	double HalfDiagonal = 0.5 * Math.sqrt(250.0 * 250.0 + 250.0 * 250.0);
	double AngleRange = 45.0;
	double AnglePrecision = 2.0;
	public static double Phi = 0.5 * (-1.0 + Math.sqrt(5.0)); // Golden Ratio
	
	public Point centroid = new Point(0, 0);
	public Rectangle boundingBox = new Rectangle(0, 0, 0, 0);
	int bounds[] = { 0, 0, 0, 0 };
	
	Vector Templates = new Vector(NumTemplates);

	public static final int GESTURES_DEFAULT = 1;
	public static final int GESTURES_SIMPLE = 2;
	public static final int GESTURES_CIRCLES = 3;
        
        private static String DIV_TYPE = "DIV";
        private static String TEXT_BOX_TYPE = "TEXT BOX";
        private static String CHECK_BOX_TYPE = "CHECK BOX";
        private static String IMAGE_TYPE = "IMAGE";
        private static String BUTTON_TYPE = "BUTTON";
        
        private static String UNDO_ACTION = "UNDO";
        private static String DELETE_ACTION = "DELETE";
        private static String TEXT_EDIT_ACTION = "TEXT EDIT";
        private static String HEADER_EDIT_ACTION = "HEADER EDIT";
	
	public Recognizer()
	{
		this(GESTURES_SIMPLE);
	}

	public Recognizer(int gestureSet)
	{
		switch(gestureSet)
		{
			case GESTURES_DEFAULT:
				loadTemplatesDefault(); break;

			case GESTURES_SIMPLE:
				loadTemplatesSimple();	break;

			case GESTURES_CIRCLES:
				loadTemplatesCircles();	break;
		}
	}
	
	void loadTemplatesDefault()
	{
		Templates.addElement(loadTemplate("triangle", TemplateData.trianglePoints));
		//Templates.addElement(loadTemplate("x", TemplateData.xPoints));
		Templates.addElement(loadTemplate("circle CCW", TemplateData.circlePointsCCW));
		Templates.addElement(loadTemplate("check", TemplateData.checkPoints));
		Templates.addElement(loadTemplate("caret CW", TemplateData.caretPointsCW));
		Templates.addElement(loadTemplate("question", TemplateData.questionPoints));
		Templates.addElement(loadTemplate("arrow", TemplateData.arrowPoints));
		Templates.addElement(loadTemplate("leftSquareBracket", TemplateData.leftSquareBracketPoints));
		Templates.addElement(loadTemplate("rightSquareBracket", TemplateData.rightSquareBracketPoints));
		Templates.addElement(loadTemplate("v", TemplateData.vPoints));	
		Templates.addElement(loadTemplate("leftCurlyBrace", TemplateData.leftCurlyBracePoints));
		Templates.addElement(loadTemplate("rightCurlyBrace", TemplateData.rightCurlyBracePoints));
		Templates.addElement(loadTemplate("star", TemplateData.starPoints));
		Templates.addElement(loadTemplate("pigTail", TemplateData.pigTailPoints));
                
                Templates.addElement(loadTemplate(DIV_TYPE, TemplateData.rectanglePointsCCW));
                Templates.addElement(loadTemplate(IMAGE_TYPE, TemplateData.imagePoints));
                Templates.addElement(loadTemplate(TEXT_BOX_TYPE, TemplateData.textBoxPoints));
                Templates.addElement(loadTemplate(UNDO_ACTION, TemplateData.undoPoints));
                Templates.addElement(loadTemplate(BUTTON_TYPE, TemplateData.buttonPoints));
                Templates.addElement(loadTemplate(CHECK_BOX_TYPE, TemplateData.checkBoxPoints));
                Templates.addElement(loadTemplate(DELETE_ACTION, TemplateData.deletePoints));
                Templates.addElement(loadTemplate(TEXT_EDIT_ACTION, TemplateData.textEditPoints));
                Templates.addElement(loadTemplate(HEADER_EDIT_ACTION, TemplateData.headerEditPoints));
	}
	
	void loadTemplatesSimple()
	{
		Templates.addElement(loadTemplate("circle CCW", TemplateData.circlePointsCCW));
		Templates.addElement(loadTemplate("circle CW", TemplateData.circlePointsCW));
		Templates.addElement(loadTemplate(DIV_TYPE, TemplateData.rectanglePointsCCW));
		Templates.addElement(loadTemplate(DIV_TYPE, TemplateData.rectanglePointsCCW));
		Templates.addElement(loadTemplate("caret CCW", TemplateData.caretPointsCCW));
		Templates.addElement(loadTemplate("caret CW", TemplateData.caretPointsCW));
	}
	

	void loadTemplatesCircles()
	{
		Templates.addElement(loadTemplate("circle CCW", TemplateData.circlePointsCCW));
		Templates.addElement(loadTemplate("circle CW", TemplateData.circlePointsCW));
	}
	
	Template loadTemplate(String name, int[] array)
	{
		return new Template(name, loadArray(array));
	}
	
	Vector loadArray(int[] array)
	{
		Vector v = new Vector(array.length/2);
		for (int i = 0; i < array.length; i+= 2)
		{
			Point p = new Point(array[i], array[i+1]);
			v.addElement(p);
		}
		
	//	System.out.println(v.size() + " " + array.length);
	
		return v;
	}
	
	public Result Recognize(Vector points)
	{
		points = Utils.Resample(points, NumPoints);		
		points = Utils.RotateToZero(points, centroid, boundingBox);
		points = Utils.ScaleToSquare(points, SquareSize);
		points = Utils.TranslateToOrigin(points);
	
		bounds[0] = (int)boundingBox.X;
		bounds[1] = (int)boundingBox.Y;
		bounds[2] = (int)boundingBox.X + (int)boundingBox.Width;
		bounds[3] = (int)boundingBox.Y + (int)boundingBox.Height;
		
		int t = 0;
		
		double b = Double.MAX_VALUE;
		for (int i = 0; i < Templates.size(); i++)
		{
			double d = Utils.DistanceAtBestAngle(points, (Template)Templates.elementAt(i), -AngleRange, AngleRange, AnglePrecision);
			if (d < b)
			{
				b = d;
				t = i;
			}
		}
		double score = 1.0 - (b / HalfDiagonal);
		return new Result(((Template)Templates.elementAt(t)).Name, score, t);
	};

	int AddTemplate(String name, Vector points)
	{
		Templates.addElement(new Template(name, points));
		return Templates.size();
	}
	
	int DeleteUserTemplates()
	{
		for (int i = Templates.size()-NumTemplates; i > 0; i--)
		{
			Templates.removeElementAt(Templates.size()-1);
		}
		
		return Templates.size();
	}

}
