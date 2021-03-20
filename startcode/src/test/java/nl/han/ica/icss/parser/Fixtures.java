package nl.han.ica.icss.parser;

import nl.han.ica.icss.ast.*;
import nl.han.ica.icss.ast.literals.*;
import nl.han.ica.icss.ast.operations.AddOperation;
import nl.han.ica.icss.ast.operations.MultiplyOperation;
import nl.han.ica.icss.ast.operations.SubtractOperation;
import nl.han.ica.icss.ast.selectors.ClassSelector;
import nl.han.ica.icss.ast.selectors.IdSelector;
import nl.han.ica.icss.ast.selectors.TagSelector;
import org.checkerframework.checker.units.qual.A;

import javax.swing.text.Style;

public class Fixtures {

    public static AST uncheckedLevel0() {
        Stylesheet stylesheet = new Stylesheet();
		/*
		p {
			background-color: #ffffff;
			width: 500px;
		}
		*/
        stylesheet.addChild((new Stylerule())
                .addChild(new TagSelector("p"))
                .addChild((new Declaration("background-color"))
                        .addChild(new ColorLiteral("#ffffff")))
                .addChild((new Declaration("width"))
                        .addChild(new PixelLiteral("500px")))
        );
		/*
		a {
			color: #ff0000;
		}
		*/
        stylesheet.addChild((new Stylerule())
                .addChild(new TagSelector("a"))
                .addChild((new Declaration("color"))
                        .addChild(new ColorLiteral("#ff0000")))
        );
		/*
		#menu {
			width: 520px;
		}
		*/
        stylesheet.addChild((new Stylerule())
                .addChild(new IdSelector("#menu"))
                .addChild((new Declaration("width"))
                        .addChild(new PixelLiteral("520px")))
        );
		/*
		.menu {
			color: #000000;
		}
		*/
        stylesheet.addChild((new Stylerule())
                .addChild(new ClassSelector(".menu"))
                .addChild((new Declaration("color"))
                        .addChild(new ColorLiteral("#000000")))
        );

        return new AST(stylesheet);
    }

    public static AST uncheckedLevel1() {
        Stylesheet stylesheet = new Stylesheet();
		/*
			LinkColor := #ff0000;
			ParWidth := 500px;
			AdjustColor := TRUE;
			UseLinkColor := FALSE;
		 */
        stylesheet.addChild((new VariableAssignment())
                .addChild(new VariableReference("LinkColor"))
                .addChild(new ColorLiteral("#ff0000"))
        );
        stylesheet.addChild((new VariableAssignment())
                .addChild(new VariableReference("ParWidth"))
                .addChild(new PixelLiteral("500px"))
        );
        stylesheet.addChild((new VariableAssignment())
                .addChild(new VariableReference("AdjustColor"))
                .addChild(new BoolLiteral(true))
        );
        stylesheet.addChild((new VariableAssignment())
                .addChild(new VariableReference("UseLinkColor"))
                .addChild(new BoolLiteral(false))
        );
   	    /*
	        p {
	        background-color: #ffffff;
	        width: ParWidth;
            }
	    */
        stylesheet.addChild((new Stylerule())
                .addChild(new TagSelector("p"))
                .addChild((new Declaration("background-color"))
                        .addChild(new ColorLiteral("#ffffff")))
                .addChild((new Declaration("width"))
                        .addChild(new VariableReference("ParWidth")))
        );
        /*
        a {
	        color: LinkColor;
        }
        */
        stylesheet.addChild((new Stylerule())
                .addChild(new TagSelector("a"))
                .addChild((new Declaration("color"))
                        .addChild(new VariableReference("LinkColor")))
        );
        /*
            #menu {
	            width: 520px;
            }
        */
        stylesheet.addChild((new Stylerule())
                .addChild(new IdSelector("#menu"))
                .addChild((new Declaration("width"))
                        .addChild(new PixelLiteral("520px")))
        );
        /*
            .menu {
	            color: #000000;
            }
        */
        stylesheet.addChild((new Stylerule())
                .addChild(new ClassSelector(".menu"))
                .addChild((new Declaration("color"))
                        .addChild(new ColorLiteral("#000000")))
        );
        return new AST(stylesheet);
    }

    public static AST uncheckedLevel2() {
        Stylesheet stylesheet = new Stylesheet();
		/*
			LinkColor := #ff0000;
			ParWidth := 500px;
			AdjustColor := TRUE;
			UseLinkColor := FALSE;
		 */
        stylesheet.addChild((new VariableAssignment())
                .addChild(new VariableReference("LinkColor"))
                .addChild(new ColorLiteral("#ff0000"))
        );
        stylesheet.addChild((new VariableAssignment())
                .addChild(new VariableReference("ParWidth"))
                .addChild(new PixelLiteral("500px"))
        );
        stylesheet.addChild((new VariableAssignment())
                .addChild(new VariableReference("AdjustColor"))
                .addChild(new BoolLiteral(true))
        );
        stylesheet.addChild((new VariableAssignment())
                .addChild(new VariableReference("UseLinkColor"))
                .addChild(new BoolLiteral(false))
        );
   	    /*
	        p {
	        background-color: #ffffff;
	        width: ParWidth;
            }
	    */
        stylesheet.addChild((new Stylerule())
                .addChild(new TagSelector("p"))
                .addChild((new Declaration("background-color"))
                        .addChild(new ColorLiteral("#ffffff")))
                .addChild((new Declaration("width"))
                        .addChild(new VariableReference("ParWidth")))
        );
        /*
        a {
	        color: LinkColor;
        }
        */
        stylesheet.addChild((new Stylerule())
                .addChild(new TagSelector("a"))
                .addChild((new Declaration("color"))
                        .addChild(new VariableReference("LinkColor")))
        );
        /*
            #menu {
        	width: ParWidth + 2 * 10px;
            }
        */
        stylesheet.addChild((new Stylerule())
                .addChild(new IdSelector("#menu"))
                .addChild((new Declaration("width"))
                        .addChild((new AddOperation())
                                .addChild(new VariableReference("ParWidth"))
                                .addChild((new MultiplyOperation())
                                        .addChild(new ScalarLiteral("2"))
                                        .addChild(new PixelLiteral("10px"))

                                ))));
        /*
            .menu {
	            color: #000000;
            }
        */
        stylesheet.addChild((new Stylerule())
                .addChild(new ClassSelector(".menu"))
                .addChild((new Declaration("color"))
                        .addChild(new ColorLiteral("#000000")))
        );
        return new AST(stylesheet);
    }

    public static AST uncheckedLevel3() {
        Stylesheet stylesheet = new Stylesheet();
		/*
			LinkColor := #ff0000;
			ParWidth := 500px;
			AdjustColor := TRUE;
			UseLinkColor := FALSE;
		 */
        stylesheet.addChild((new VariableAssignment())
                .addChild(new VariableReference("LinkColor"))
                .addChild(new ColorLiteral("#ff0000"))
        );
        stylesheet.addChild((new VariableAssignment())
                .addChild(new VariableReference("ParWidth"))
                .addChild(new PixelLiteral("500px"))
        );
        stylesheet.addChild((new VariableAssignment())
                .addChild(new VariableReference("AdjustColor"))
                .addChild(new BoolLiteral(true))
        );
        stylesheet.addChild((new VariableAssignment())
                .addChild(new VariableReference("UseLinkColor"))
                .addChild(new BoolLiteral(false))
        );
   	    /*
	        p {
				background-color: #ffffff;
				width: ParWidth;
				if[AdjustColor] {
	    			color: #124532;
	    			if[UseLinkColor]{
	        			bg-color: LinkColor;
	    			}
				}
			}
			p {
				background-color: #ffffff;
				width: ParWidth;
				if[AdjustColor] {
	    			color: #124532;
                    if[UseLinkColor]{
                        background-color: LinkColor;
                    } else {
                        background-color: #000000;
                    }
                    height: 20px;
                }
}
	    */
        stylesheet.addChild((new Stylerule())
                .addChild(new TagSelector("p"))
                .addChild((new Declaration("background-color"))
                        .addChild(new ColorLiteral("#ffffff")))
                .addChild((new Declaration("width"))
                        .addChild(new VariableReference("ParWidth")))
                .addChild((new IfClause())
                        .addChild(new VariableReference("AdjustColor"))
                        .addChild((new Declaration("color")
                                .addChild(new ColorLiteral("#124532"))))
                        .addChild((new IfClause())
                                .addChild(new VariableReference("UseLinkColor"))
                                .addChild(new Declaration("background-color").addChild(new VariableReference("LinkColor")))
                                .addChild((new ElseClause())
                                        .addChild(new Declaration("background-color").addChild(new ColorLiteral("#000000")))

                                )
                        ))
                .addChild((new Declaration("height"))
                        .addChild(new PixelLiteral("20px")))
        );
        /*
        a {
	        color: LinkColor;
        }
        */
        stylesheet.addChild((new Stylerule())
                .addChild(new TagSelector("a"))
                .addChild((new Declaration("color"))
                        .addChild(new VariableReference("LinkColor"))
                )
        );
        /*
            #menu {
        	width: ParWidth + 20px;
            }
        */
        stylesheet.addChild((new Stylerule())
                .addChild(new IdSelector("#menu"))
                .addChild((new Declaration("width"))
                        .addChild((new AddOperation())
                                .addChild(new VariableReference("ParWidth"))
                                .addChild(new PixelLiteral("20px"))
                        )
                )
        );
        /*


         .menu {
				color: #000000;
    			background-color: LinkColor;

			}1

        */
        stylesheet.addChild((new Stylerule())
                .addChild(new ClassSelector(".menu"))

                .addChild((new Declaration("color"))
                        .addChild(new ColorLiteral("#000000"))
                )
                .addChild((new Declaration("background-color"))
                        .addChild(new VariableReference("LinkColor"))
                )

        );

        return new AST(stylesheet);
    }

    public static AST uncheckedTest1() {
        Stylesheet stylesheet = new Stylesheet();
    	/*
    		Color := #ff0000;
			Pixel := 500px;
			Percentage := 75%;
			Scalar := 13;
			Color2 := Color;
    	 */
        stylesheet.addChild((new VariableAssignment())
                .addChild(new VariableReference("Color"))
                .addChild(new ColorLiteral("#ff0000"))
        );
        stylesheet.addChild((new VariableAssignment())
				.addChild(new VariableReference("Pixel"))
                .addChild(new PixelLiteral("500px")));
        stylesheet.addChild((new VariableAssignment())
                .addChild(new VariableReference("Percentage"))
                .addChild(new PercentageLiteral("75%")));
        stylesheet.addChild((new VariableAssignment())
                .addChild(new VariableReference("Scalar"))
                .addChild(new ScalarLiteral("13"))
        );
        stylesheet.addChild((new VariableAssignment())
                .addChild(new VariableReference("Color2"))
                .addChild(new VariableReference("Color"))
        );
        /*
            .var-assignment-and-usage {
                Extra := 50px;
                color: Color;
                width: Pixel;
                height: Percentage;
                background-color: Scalar;
                width: Extra;
            }
         */
        stylesheet.addChild((new Stylerule())
                .addChild(new ClassSelector(".var-assignment-and-usage"))
                .addChild((new VariableAssignment())
                        .addChild(new VariableReference("Extra"))
                        .addChild(new PixelLiteral("50px"))
                )
                .addChild((new Declaration("color"))
                        .addChild(new VariableReference("Color")))
                .addChild((new Declaration("width"))
                        .addChild(new VariableReference("Pixel")))
                .addChild((new Declaration("height"))
                        .addChild(new VariableReference("Percentage")))
                .addChild((new Declaration("background-color"))
                        .addChild(new VariableReference("Scalar")))
                .addChild((new Declaration("width"))
                        .addChild(new VariableReference("Extra")))
        );
        /*
            #double-ref {
                color: Color2;
            }
         */
        stylesheet.addChild((new Stylerule())
                .addChild(new IdSelector("#double-ref"))
                .addChild((new Declaration("color"))
                        .addChild(new VariableReference("Color2"))
                )
        );
        return new AST(stylesheet);
    }

    public static AST uncheckedTest2() {
        Stylesheet stylesheet = new Stylesheet();
        /*
            TestColor := #ff0000;
            Condition := TRUE;
         */
        stylesheet.addChild((new VariableAssignment())
                .addChild(new VariableReference("TestColor"))
                .addChild(new ColorLiteral("#ff0000"))
        );
        stylesheet.addChild((new VariableAssignment())
                .addChild(new VariableReference("Condition"))
                .addChild(new BoolLiteral("TRUE"))
        );

        /*
            .var-assignment-and-use-in-if-statement {
                color: TestColor;
                if[Condition] {
                    ScreenWidth := 1920px;
                    width: ScreenWidth;
                } else {
                    ScreenHeight := 768px;
                    height: ScreenHeight;
                }
            }
         */
        stylesheet.addChild((new Stylerule())
                .addChild(new ClassSelector(".var-assignment-and-use-in-if-statement"))
                .addChild((new Declaration("color"))
                        .addChild(new VariableReference("TestColor")))
                .addChild((new IfClause())
                        .addChild(new VariableReference("Condition"))
                        .addChild((new VariableAssignment())
                                .addChild(new VariableReference("ScreenWidth"))
                                .addChild(new PixelLiteral("1920px")))
                        .addChild((new Declaration("width"))
                                .addChild(new VariableReference("ScreenWidth")))
                        .addChild((new ElseClause())
                                .addChild((new VariableAssignment())
                                        .addChild(new VariableReference("ScreenHeight"))
                                        .addChild(new PixelLiteral("768px")))
                                .addChild((new Declaration("height"))
                                        .addChild(new VariableReference("ScreenHeight")))
                        )
                )

        );

        return new AST(stylesheet);
    }

    public static AST uncheckedTest3() {
        Stylesheet stylesheet = new Stylesheet();
        /*
            Scalar := 100;
            Percentage := 20%;
         */
        stylesheet.addChild((new VariableAssignment())
                .addChild(new VariableReference("Scalar"))
                .addChild(new ScalarLiteral("100"))
        );
        stylesheet.addChild((new VariableAssignment())
                .addChild(new VariableReference("Percentage"))
                .addChild(new PercentageLiteral("20%"))
        );
        /*
            .operation-abuse-test {
                height: 100% - 12%;
                width: 3 * 100px + 600px;
            }
         */
        stylesheet.addChild((new Stylerule())
                .addChild(new ClassSelector(".operation-abuse-test"))
                .addChild((new Declaration("height"))
                        .addChild((new SubtractOperation())
                                .addChild(new PercentageLiteral("100%"))
                                .addChild(new PercentageLiteral("12%"))))
                .addChild((new Declaration("width"))
                        .addChild((new MultiplyOperation())
                                .addChild(new ScalarLiteral(3))
                                .addChild((new AddOperation())
                                        .addChild(new PixelLiteral("100px"))
                                        .addChild(new PixelLiteral("600px")))))
        );
        /*
            ScreenWidth := 1280px;
         */
        stylesheet.addChild((new VariableAssignment())
                .addChild(new VariableReference("ScreenWidth"))
                .addChild(new PixelLiteral("1280px"))
        );

        /*
            .operation-abuse-with-vars-test {
                height: Scalar * Percentage;
                width: ScreenWidth * Percentage + Scalar;
            }
         */
        stylesheet.addChild((new Stylerule())
                .addChild(new ClassSelector(".operation-abuse-with-vars-test"))
                .addChild((new Declaration("height"))
                        .addChild((new MultiplyOperation())
                                .addChild(new VariableReference("Scalar"))
                                .addChild(new VariableReference("Percentage"))))
                .addChild((new Declaration("width"))
                        .addChild((new MultiplyOperation())
                                .addChild(new VariableReference("ScreenWidth"))
                                .addChild((new AddOperation())
                                        .addChild(new VariableReference("Percentage"))
                                        .addChild(new VariableReference("Scalar")))))
        );

        return new AST(stylesheet);
    }

}