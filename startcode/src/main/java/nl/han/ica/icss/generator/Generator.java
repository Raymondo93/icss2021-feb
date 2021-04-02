package nl.han.ica.icss.generator;


import nl.han.ica.icss.ast.*;
import nl.han.ica.icss.ast.literals.ColorLiteral;
import nl.han.ica.icss.ast.literals.PercentageLiteral;
import nl.han.ica.icss.ast.literals.PixelLiteral;
import nl.han.ica.icss.ast.literals.ScalarLiteral;
import nl.han.ica.icss.ast.selectors.ClassSelector;
import nl.han.ica.icss.ast.selectors.IdSelector;
import nl.han.ica.icss.ast.selectors.TagSelector;

public class Generator {

	public String generate(AST ast) {
        String styleSheet =  "";
		for (ASTNode node: ast.root.getChildren()) {
			if (node instanceof Stylerule) {
				styleSheet = styleSheet.concat(generateStylerule(node));
			}
		}
		return styleSheet;
	}

	private String generateStylerule(ASTNode node) {
		String styleRule = "";
		for (ASTNode child : node.getChildren()) {
			if (child instanceof Selector) {
				String selector = getSelector((Selector) child);
				styleRule = styleRule.concat(selector);
			} else if (child instanceof Declaration) {
				String declarations = generateDeclarations(child);
				styleRule = styleRule.concat(declarations);
			}
		}
		styleRule = styleRule.concat("} \n \n");
		return styleRule;
	}

	private String generateDeclarations(ASTNode node) {
		String declaration = "";
        for (ASTNode child : node.getChildren()) {
            if (child instanceof PropertyName) {
                PropertyName property = (PropertyName) child;
                declaration = declaration.concat("  " + property.name + ": ");
            } else if (child instanceof Literal) {
                String literal = getLiteral((Literal) child);
                declaration = declaration.concat(literal);
            }
        }
        return declaration;
	}

	private String getLiteral(Literal literal) {
		if (literal instanceof ColorLiteral) {
            return ((ColorLiteral) literal).value + "; \n";
        } else if (literal instanceof PercentageLiteral) {
            return ((PercentageLiteral) literal).value + "%; \n";
        } else if (literal instanceof PixelLiteral) {
            return ((PixelLiteral) literal).value + "px; \n";
        } else {
            return ((ScalarLiteral) literal).value + "; \n";
        }
	}

	private String getSelector(Selector selector) {
        if (selector instanceof ClassSelector) {
            return ((ClassSelector) selector).cls + " { \n";
        } else if (selector instanceof IdSelector) {
            return ((IdSelector) selector).id + " { \n";
        } else {
            return ((TagSelector) selector).tag + " { \n";
        }
	}


}
