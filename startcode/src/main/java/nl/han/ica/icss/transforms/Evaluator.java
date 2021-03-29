package nl.han.ica.icss.transforms;

import nl.han.ica.datastructures.*;
import nl.han.ica.icss.ast.*;
import nl.han.ica.icss.ast.literals.*;
import nl.han.ica.icss.ast.operations.AddOperation;
import nl.han.ica.icss.ast.operations.MultiplyOperation;
import nl.han.ica.icss.ast.operations.SubtractOperation;
import nl.han.ica.icss.ast.selectors.ClassSelector;
import nl.han.ica.icss.ast.selectors.IdSelector;
import nl.han.ica.icss.ast.selectors.TagSelector;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Evaluator implements Transform {

    private static IHANLinkedList<HashMap<String, Literal>> globalVariableValues;

    public Evaluator() {
        globalVariableValues = new HANLinkedList<>();
    }

    @Override
    public void apply(AST ast) {
        ast.setRoot(generateAST(ast));
    }

    private Stylesheet generateAST(AST ast) {
        Stylesheet stylesheet = new Stylesheet();
        for (ASTNode parent : ast.root.getChildren()) {
            if (parent instanceof VariableAssignment) {
                handleGlobalVariableAssignment((VariableAssignment) parent, globalVariableValues);
            } else if (parent instanceof Stylerule) {

                Stylerule rule = new Stylerule();
                IHANLinkedList<IHANLinkedList<HashMap<String, Literal>>> localVariables =
                        new HANLinkedList<>();

                for (ASTNode child : parent.getChildren()) {
                    localVariables.addFirst(new HANLinkedList<>());

                    if (child instanceof IfClause) {
                        try {
                            ArrayList<ASTNode> ifClauseBody = this.evaluateIfStatement(rule, (IfClause) child, localVariables);
                            for (ASTNode node : ifClauseBody) {
                                rule.addChild(this.getChild(node, localVariables));
                            }
                        } catch (Exception e) {
                            child.setError(e.getMessage());
                        }
                    } else if (child instanceof VariableAssignment) {
                        handleLocalvariable((VariableAssignment) child, localVariables);
                    } else {
                        try {
                            rule.addChild(this.getChild(child, localVariables));
                        } catch (Exception e) {
                            child.setError(e.getMessage());
                        }
                    }

                    localVariables.removeFirst();
                }
                stylesheet.addChild(rule);
            }
        }
        return stylesheet;
    }

    private ASTNode getChild(ASTNode node, IHANLinkedList<IHANLinkedList<HashMap<String, Literal>>> localVariables) throws Exception {
        if (node instanceof Selector) {
            return this.getSelector((Selector) node);
        } else if (node instanceof Declaration) {
            return this.getDeclaration((Declaration) node, localVariables);
        } else if (node instanceof PropertyName) {
            return new PropertyName(((PropertyName) node).name);
        } else if (node instanceof Expression) {
            return this.getLiteral((Expression) node);
        }
        throw new Exception("Child not found");
    }

    private void handleLocalvariable(VariableAssignment node, IHANLinkedList<IHANLinkedList<HashMap<String, Literal>>> localVariables) {
        for (int i = 0; i < localVariables.getSize(); ++i) {
            for (int j = 0; j < localVariables.get(i).getSize(); ++j) {
                if (localVariables.get(i).get(j).containsKey(node.name.name)) {
                    try {
                        localVariables.get(i).get(j).put(node.name.name, getLiteral(node.expression));
                        return;
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                    }
                }
            }
        }
        handleGlobalVariableAssignment(node, globalVariableValues);
    }

    /**
     * Author: Raymond de Bruine
     * Function:    First loop through variables to check if variable exists. If variable exists, replace value
     * If not exists, add new variable to the linkedlist
     *
     * @param node
     * @param variableList
     */
    private void handleGlobalVariableAssignment(VariableAssignment node, IHANLinkedList<HashMap<String, Literal>> variableList) {
        for (int i = 0; i < variableList.getSize(); ++i) {
            if (variableList.get(i).containsKey(node.name.name)) {
                try {
                    variableList.get(i).put(node.name.name, getLiteral(node.expression));
                    return;
                } catch (Exception e) {
                    node.setError(e.getMessage());
                }
            }
        }
        addVariableToList(node, variableList);
    }

    private void addVariableToList(VariableAssignment node, IHANLinkedList<HashMap<String, Literal>> variableList) {
        try {
            HashMap<String, Literal> newVariable = new HashMap<>();
            if (node.expression instanceof VariableReference) {
                newVariable.put(node.name.name, getVariableReferenceValue((VariableReference) node.expression, new HANLinkedList<>()));
            } else {
                newVariable.put(node.name.name, getLiteral(node.expression));
            }
            variableList.addFirst(newVariable);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            node.setError(e.getMessage());
        }
    }

    private ArrayList<ASTNode> evaluateIfStatement(ASTNode parent, IfClause ifClause, IHANLinkedList<IHANLinkedList<HashMap<String, Literal>>> localVariables) throws Exception {
        ArrayList<ASTNode> ifClauseBody = new ArrayList<>();
        if (this.checkConditionalExpression(ifClause.conditionalExpression, localVariables)) {
            localVariables.addFirst(new HANLinkedList<>());
            for (ASTNode child : ifClause.body) {
                System.out.println("gaatie op zn bek?");
                if (child instanceof IfClause) {
                    ifClauseBody.addAll(evaluateIfStatement(ifClause, (IfClause) child, localVariables));
                } else if (child instanceof VariableAssignment) {
                    handleLocalvariable((VariableAssignment) child, localVariables);
                } else {
                    ifClauseBody.add(child);
                }
            }
        } else if (!this.checkConditionalExpression(ifClause.conditionalExpression, localVariables) && ifClause.elseClause != null) {
            localVariables.addFirst(new HANLinkedList<>());
            for (ASTNode child : ifClause.elseClause.body) {
                if (child instanceof IfClause) {
                    ifClauseBody.addAll(evaluateIfStatement(ifClause.elseClause, (IfClause) child, localVariables));
                } else if (child instanceof VariableAssignment) {
                    handleLocalvariable((VariableAssignment) child, localVariables);
                } else {
                    ifClauseBody.add(child);
                }
            }
        }
        return ifClauseBody;
    }

    private boolean checkConditionalExpression(Expression expression, IHANLinkedList<IHANLinkedList<HashMap<String, Literal>>> localVariables) throws Exception {
        if (expression instanceof VariableReference) {
            Literal value = getVariableReferenceValue((VariableReference) expression, localVariables);
            if (value instanceof BoolLiteral) {
                return ((BoolLiteral) value).value;
            }
        }
        return ((BoolLiteral) getLiteral(expression)).value;
    }

    private Literal getLiteral(Expression node) throws Exception {
        if (node instanceof ColorLiteral) {
            return new ColorLiteral(((ColorLiteral) node).value);
        } else if (node instanceof BoolLiteral) {
            return new BoolLiteral(((BoolLiteral) node).value);
        } else if (node instanceof PercentageLiteral) {
            return new PercentageLiteral(((PercentageLiteral) node).value);
        } else if (node instanceof PixelLiteral) {
            return new PixelLiteral(((PixelLiteral) node).value);
        } else if (node instanceof ScalarLiteral) {
            return new ScalarLiteral(((ScalarLiteral) node).value);
        }
        throw new Exception("Could not cast Expression " + node.getNodeLabel() + " to Literal.");
    }

    private Declaration getDeclaration(Declaration node, IHANLinkedList<IHANLinkedList<HashMap<String, Literal>>> localVariables) {
        Declaration declaration = new Declaration();
        try {
            for (ASTNode child : node.getChildren()) {
                if (child instanceof VariableReference) {
                    declaration.addChild(this.getVariableReferenceValue((VariableReference) child, localVariables));
                } else if (child instanceof Operation) {
                    Literal literal = handleOperation((Operation) child, localVariables);
                    System.out.println("uitslag na het rekenen + " + literal);
                    declaration.addChild(literal);
                } else {
                    declaration.addChild(this.getChild(child, localVariables));
                }
            }
        } catch(Exception e){
            ASTNode newNode = new ASTNode();
            newNode.setError(e.getMessage());
            declaration.addChild(newNode);
        }
        return declaration;
    }

    private Literal handleOperation(Operation node, IHANLinkedList<IHANLinkedList<HashMap<String, Literal>>> localVariablesList) throws Exception {
        if (node.isNestedOperation()) {
            IHANStack<Operation> operationHANStack = new HANStack<>();
            Operation currentOperation = node;
            boolean lastOperation = false;
            while (!lastOperation) {
                if (!currentOperation.isNestedOperation()) {
                    if (operationHANStack.isEmpty() || (operationHANStack.peek().lhs != null && operationHANStack.peek().rhs != null)) {
                        operationHANStack.push(currentOperation);
                    } else {
                        Literal l = handleOperation(currentOperation, localVariablesList);
                        operationHANStack.peek().addChild(l);
                    }
                    lastOperation = true;
                } else {
                    if (currentOperation instanceof MultiplyOperation) {
                        System.out.println(currentOperation.toString());
                        Literal left;
                        if (currentOperation.lhs instanceof VariableReference) {
                            left = getVariableReferenceValue((VariableReference) currentOperation.lhs, localVariablesList);
                        } else {
                            left = getLiteral(currentOperation.lhs);
                        }

                        Expression right;
                        if (currentOperation.rhs instanceof Operation) {
                            Operation nestedOperation = (Operation) currentOperation.rhs;
                            if (nestedOperation.lhs instanceof VariableReference) {
                                right = getVariableReferenceValue((VariableReference) nestedOperation.lhs, localVariablesList);
                            } else {
                                right = getLiteral(nestedOperation.lhs);
                            }
                            Operation o = new MultiplyOperation();
                            o.addChild(left);
                            o.addChild(right);
                            System.out.println(o);
                            Literal l = handleOperation(o, localVariablesList);
                            ((Operation) currentOperation.rhs).setLhs(l);
                            currentOperation = (Operation) currentOperation.rhs;
                            if (currentOperation instanceof AddOperation) {
                                operationHANStack.peek().setRhs(new AddOperation());
                            } else if (currentOperation instanceof SubtractOperation) {
                                operationHANStack.peek().setRhs(new SubtractOperation());
                            }
                        }
                    } else if (currentOperation instanceof AddOperation || currentOperation instanceof SubtractOperation) {
                        if (currentOperation instanceof AddOperation) {
                            operationHANStack.push(new AddOperation());
                        } else {
                            operationHANStack.push(new SubtractOperation());
                        }
                        Literal left = null;
                        if (currentOperation.lhs instanceof VariableReference) {
                            left = getVariableReferenceValue((VariableReference) currentOperation.lhs, localVariablesList);
                        } else if (currentOperation.lhs instanceof Literal) {
                            left = getLiteral(currentOperation.lhs);
                        }
                        operationHANStack.peek().addChild(left);
                        if (currentOperation.rhs instanceof AddOperation || currentOperation.rhs instanceof SubtractOperation) {
                            if (currentOperation.rhs instanceof AddOperation) {
                                operationHANStack.peek().addChild(new AddOperation());
                            } else {
                                operationHANStack.peek().addChild(new SubtractOperation());
                            }
                            currentOperation = (Operation) currentOperation.rhs;
                        } else if (currentOperation.rhs instanceof MultiplyOperation) {
                            currentOperation = (Operation) currentOperation.rhs;
                        }
                    }
                }
            }

            operationHANStack = swapStack(operationHANStack);

            while (!operationHANStack.isEmpty()) {
                Operation operation = operationHANStack.pop();
                System.out.println("in dde stack" + operation.toString());
                if (operation.isNestedOperation()) {
                    operation.setRhs(operationHANStack.peek().lhs);
                    Literal value = handleOperation(operation, localVariablesList);
                    operationHANStack.peek().setLhs(value);
                } else {
                    return handleOperation(operation, localVariablesList);
                }
            }
        } else {
            Literal lhs;
            Literal rhs;
            if (node.lhs instanceof VariableReference) {
                lhs = this.getVariableReferenceValue((VariableReference) node.lhs, localVariablesList);
            } else {
                lhs = getLiteral(node.lhs);
            }
            if (node.rhs instanceof VariableReference) {
                rhs = this.getVariableReferenceValue((VariableReference) node.lhs, localVariablesList);
            } else {
                rhs = this.getLiteral(node.rhs);
            }
            if (node instanceof AddOperation) {
                return addOperation(lhs, rhs);
            } else if (node instanceof SubtractOperation) {
                return subOperation(lhs, rhs);
            } else if (node instanceof MultiplyOperation) {
                return mulOperation(lhs, rhs);
            }
        }
        throw new Exception("Something went wrong in the node! This should not happen! You hacked the system");
    }


    private Literal mulOperation(Literal lhs, Literal rhs) throws Exception {
        if (lhs instanceof ScalarLiteral && rhs instanceof ScalarLiteral) {
            return new ScalarLiteral(((ScalarLiteral) lhs).value * ((ScalarLiteral) rhs).value);
        } else if ((lhs instanceof ScalarLiteral && rhs instanceof PixelLiteral)) {
            return new PixelLiteral(((ScalarLiteral) lhs).value * ((PixelLiteral) rhs).value);
        } else if ((lhs instanceof PixelLiteral && rhs instanceof ScalarLiteral)) {
            return new PixelLiteral(((PixelLiteral) lhs).value * ((ScalarLiteral) rhs).value);
        } else if ((lhs instanceof ScalarLiteral && rhs instanceof PercentageLiteral)) {
            return new PercentageLiteral(((ScalarLiteral) lhs).value * ((PercentageLiteral) rhs).value);
        } else if ((lhs instanceof PercentageLiteral && rhs instanceof ScalarLiteral)) {
            return new PercentageLiteral(((PercentageLiteral) lhs).value * ((ScalarLiteral) rhs).value);
        }
        throw new Exception("Something went wrong in the operation, this should not happen! You hacked the system!");
    }

    private Literal subOperation(Literal lhs, Literal rhs) throws Exception {
        if (lhs instanceof ScalarLiteral && rhs instanceof ScalarLiteral) {
            return new ScalarLiteral(((ScalarLiteral) lhs).value - ((ScalarLiteral) rhs).value);
        } else if (lhs instanceof PixelLiteral && rhs instanceof PixelLiteral) {
            return new PixelLiteral(((PixelLiteral) lhs).value - ((PixelLiteral) rhs).value);
        } else if (lhs instanceof PercentageLiteral && rhs instanceof PercentageLiteral) {
            return new PercentageLiteral(((PercentageLiteral) lhs).value - ((PercentageLiteral) rhs).value);
        }
        throw new Exception("Literal not found in substract operation.");
    }

    private Literal addOperation(Literal lhs, Literal rhs) throws Exception {
        if (lhs instanceof ScalarLiteral && rhs instanceof ScalarLiteral) {
            return new ScalarLiteral(((ScalarLiteral) lhs).value + ((ScalarLiteral) rhs).value);
        } else if (lhs instanceof PixelLiteral && rhs instanceof PixelLiteral) {
            return new PixelLiteral(((PixelLiteral) lhs).value + ((PixelLiteral) rhs).value);
        } else if (lhs instanceof PercentageLiteral && rhs instanceof PercentageLiteral) {
            return new PercentageLiteral(((PercentageLiteral) lhs).value + ((PercentageLiteral) rhs).value);
        }
        throw new Exception("Literals not found in add operation");
    }

    private Selector getSelector(Selector node) {
        if (node instanceof TagSelector) {
            return new TagSelector(((TagSelector) node).tag);
        } else if (node instanceof ClassSelector) {
            return new ClassSelector(((ClassSelector) node).cls);
        } else {
            return new IdSelector(((IdSelector) node).id);
        }
    }

    private Literal getVariableReferenceValue(VariableReference reference, IHANLinkedList<IHANLinkedList<HashMap<String, Literal>>> localVariables) throws Exception {
        for (int i = 0; i < localVariables.getSize(); ++i) {
            for (int j = 0; j < localVariables.get(i).getSize(); ++j) {
                if (localVariables.get(i).get(j).containsKey(reference.name)) {
                    return localVariables.get(i).get(j).get(reference.name);
                }
            }
        }
        for (int i = 0; i < globalVariableValues.getSize(); ++i) {
            if (globalVariableValues.get(i).containsKey(reference.name)) {
                return globalVariableValues.get(i).get(reference.name);
            }
        }
        throw new Exception("Variable " + reference.name + " not found.");
    }

    private IHANStack<Operation> swapStack(IHANStack<Operation> operations) {
        IHANStack<Operation> o = new HANStack<>();
        while (!operations.isEmpty()) {
            o.push(operations.pop());
        }
        return o;
    }

}

