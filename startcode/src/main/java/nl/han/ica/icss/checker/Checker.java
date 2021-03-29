package nl.han.ica.icss.checker;

import nl.han.ica.datastructures.HANLinkedList;
import nl.han.ica.datastructures.IHANLinkedList;
import nl.han.ica.icss.ast.*;
import nl.han.ica.icss.ast.literals.*;
import nl.han.ica.icss.ast.operations.AddOperation;
import nl.han.ica.icss.ast.operations.MultiplyOperation;
import nl.han.ica.icss.ast.operations.SubtractOperation;
import nl.han.ica.icss.ast.types.ExpressionType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Checker {

    private static IHANLinkedList<HashMap<String, ExpressionType>> globalVariablesList;
    private static Map<String, List<ExpressionType>> propertyBusinessRulesList;

    public Checker() {
        globalVariablesList = new HANLinkedList<>();
        propertyBusinessRulesList = new HashMap<>();
        this.setPropertyExpressionTypes();
    }

    /**
     * Author: Raymond de Bruine
     * Date: 08-03-2021
     * Loop through the AST to check the Semantics of the AST with several functions
     * @param ast => The abstract syntax tree
     */
    public void check(AST ast) {
        for (ASTNode parent: ast.root.getChildren()) {
            if (parent instanceof VariableAssignment) {
                if (this.checkIfVariableIsNew(((VariableAssignment) parent).name)) {
                    this.setVariableInList((VariableAssignment) parent, globalVariablesList);
                } else {
                    this.updateGlobalVariable((VariableAssignment) parent);
                }
            } else if (parent instanceof Stylerule) {
                IHANLinkedList<IHANLinkedList<HashMap<String, ExpressionType>>> localVariables = new HANLinkedList<>();
                localVariables.addFirst(new HANLinkedList<>());
                for (ASTNode child: parent.getChildren()) {
                    this.checkTreeNode(child, localVariables);
                }
            }
        }
    }

    /**
     * Author: Raymond de Bruine
     * Date: 08-03-2021
     * Recursive function which checks the tree node
     * @param node => ASTNode which has to be checked
     */
    private void checkTreeNode(ASTNode node, IHANLinkedList<IHANLinkedList<HashMap<String, ExpressionType>>> localVariablesListList) {
        if (node instanceof VariableAssignment) {
            if (this.checkIfVariableIsNew(((VariableAssignment) node).name, localVariablesListList)) {
                this.setVariableInList((VariableAssignment) node, localVariablesListList.getFirst());
            } else {
                this.updateLocalVariable((VariableAssignment) node, localVariablesListList);
            }
        } else if (node instanceof VariableReference) {
            if (this.checkIfVariableIsNew((VariableReference) node, localVariablesListList)) {
                node.setError("Could not find initialization of variable " + ((VariableReference) node).name);
            }
        } else if (node instanceof Operation) {
//            System.out.println("hier komt die niet als goed is " + ((Operation) node).lhs + " +/*/- " + ((Operation) node).rhs);
//            this.checkOperation((Operation) node, localVariablesListList);
            this.checkOperationValues((Operation) node, localVariablesListList);
        } else if (node instanceof Declaration) {
            this.checkDeclarationValueOnProperty((Declaration) node, localVariablesListList);
        } else if (node instanceof IfClause) {
            this.checkConditionalExpressionType((IfClause) node);
            localVariablesListList.addFirst(new HANLinkedList<>());
            for (ASTNode bodyNode: ((IfClause) node).body) {
                checkTreeNode(bodyNode, localVariablesListList);
            }
            localVariablesListList.removeFirst();
            if (((IfClause) node).elseClause != null) {
                localVariablesListList.addFirst(new HANLinkedList<>());
                for (ASTNode elseClauseBodyNode: ((IfClause) node).elseClause.body) {
                    checkTreeNode(elseClauseBodyNode, localVariablesListList);
                }
                localVariablesListList.removeFirst();
            }
        }

        for (ASTNode child: node.getChildren()) {
            if (node instanceof Operation) {
                if (((Operation) node).rhs instanceof Operation) {
                    continue;
                }
            } else if (child instanceof IfClause){
                continue;
            }
            checkTreeNode(child, localVariablesListList);
        }
    }

    private void checkOperation(Operation node, IHANLinkedList<IHANLinkedList<HashMap<String, ExpressionType>>> localVariableList) {
        if (node.rhs instanceof Operation) {
            ExpressionType multiplyValue;
            if (node instanceof MultiplyOperation) {

            }
        }
    }

    private ExpressionType getExpressionTypeOfMultiplyOperation(Expression lhs, Expression rhs, IHANLinkedList<IHANLinkedList<HashMap<String, ExpressionType>>> localVariableList) {
        if (!(rhs instanceof Operation)) {
            if (getExpressionType(lhs, localVariableList).equals(getExpressionType(rhs, localVariableList))) {
                return getExpressionType(lhs, localVariableList);
            }
        } else {
            if (rhs instanceof MultiplyOperation) {
                ExpressionType rhsType = getExpressionTypeOfMultiplyOperation(((MultiplyOperation) rhs).lhs, ((MultiplyOperation) rhs).rhs, localVariableList);

            }
            if (getExpressionType(lhs, localVariableList).equals(getExpressionType(rhs,localVariableList))) {
                return getExpressionType(lhs, localVariableList);
            }
        }
        return null;
    }

    /**
     * Author: Raymond de Bruine
     * Date: 16-03-2021
     * Function: Update a variable in the lists
     * @param node => The updated variable assignment
     * @param localVariables => List with local variables
     */
    private void updateLocalVariable(VariableAssignment node, IHANLinkedList<IHANLinkedList<HashMap<String, ExpressionType>>> localVariables) {
        for (int i = 0; i < localVariables.getSize(); ++i) {
            for (int j = 0; j < localVariables.get(i).getSize(); ++j) {
                if (localVariables.get(i).get(j).containsKey(node.name.name)) {
                    localVariables.get(i).get(j).put(node.name.name, getExpressionType(node.expression, localVariables));
                    return;
                }
            }
        }
        updateGlobalVariable(node);
    }

    /**
     * Author: Raymond de Bruine
     * Date: 16-03-2021
     * Function: update global variable.
     * @param node => The updated variable assignment
     */
    private void updateGlobalVariable(VariableAssignment node) {
        for (int i = 0; i < globalVariablesList.getSize(); ++i) {
            if (globalVariablesList.get(i).containsKey(node.name.name)) {
                globalVariablesList.get(i).put(node.name.name, getExpressionType(node.expression));
            }
        }
    }


    /**
     * Author: Raymond de Bruine
     * Date: 16-03-2021
     * Function: Checks if variable is new when there are no local variables
     * @param node => The variable reference to be checked
     * @return boolean if variable is new
     */
    private boolean checkIfVariableIsNew(VariableReference node) {
        return checkIfVariableIsNew(node, new HANLinkedList<>());
    }

    /**
     * Author: Raymond de Bruine
     * Date: 15-03-2021
     * Function: Checks if variable exists. First in local scope and then global scope
     * @param node => VariableReference node
     * @param localVariables => List with local variables
     * @return boolean => true if variable exists
     */
    private boolean checkIfVariableIsNew(VariableReference node, IHANLinkedList<IHANLinkedList<HashMap<String, ExpressionType>>> localVariables) {
        for (int i = 0; i < localVariables.getSize(); ++i) {
            if (variableMapKeyExists(node.name, localVariables.get(i))) {
                return false;
            }
        }
        return !variableMapKeyExists(node.name, globalVariablesList);
    }

    /**
     * Author: Raymond de Bruine
     * Date: 08-03-2021
     * Place variable in the list
     * @param node => The variable to be inserted
     * @param variableList => The list with variables
     */
    private void setVariableInList(VariableAssignment node, IHANLinkedList<HashMap<String, ExpressionType>> variableList) {
        HashMap<String, ExpressionType> map = new HashMap<>();
        map.put(node.name.name, getExpressionType(node.expression));
        variableList.addFirst(map);
    }

    /**
     * Author: Raymond de Bruine
     * Date: 15-03-2021
     * @param name => Name of key
     * @param variableList => LinkedList with map of variables
     * @return true when key exist in map of a list
     */
    private boolean variableMapKeyExists(String name, IHANLinkedList<HashMap<String, ExpressionType>> variableList) {
        for (int i = 0; i < variableList.getSize(); ++i) {
            if (variableList.get(i).containsKey(name)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Author: Raymond de Bruine
     * Date: 10-04-2021
     * Function: Checks if the conditional expression is of type BOOL
     * @param node => The IfClause node
     */
    private void checkConditionalExpressionType(IfClause node) {
        if (getExpressionType(node.conditionalExpression) != ExpressionType.BOOL) {
            node.setError("A conditional expression in a if statement is not of type 'Boolean'!");
        }
    }

    /**
     * Author: Raymond de Bruine
     * Date: 10-03-2021
     * Function: Checks if the expression type matches with the property. ex: property "color" can only have expression type COLOR
     * @param node => The Declaration node.
     */
    private void checkDeclarationValueOnProperty(Declaration node, IHANLinkedList<IHANLinkedList<HashMap<String, ExpressionType>>> localVarsList) {
        if (node.expression instanceof Operation) return;
        if (!propertyBusinessRulesList.get(node.property.name).contains(getExpressionType(node.expression, localVarsList))) {
            node.setError("The value of property " + node.property.name + " does not have a valid expression.");
        } else if (getExpressionType(node.expression).equals(ExpressionType.SCALAR)) {
            node.setError("The value of " + node.property.name + " cannot be a scalar.");
        }
    }

    /**
     * Author: Raymond de Bruine
     * Date: 08-03-2021
     * Function: Return type of Expression
     * @param expression => Expression
     * @return => Type of Expression
     */
    private ExpressionType getExpressionType(Expression expression) {
        if (expression instanceof BoolLiteral) {
            return ExpressionType.BOOL;
        } else if (expression instanceof ColorLiteral) {
            return ExpressionType.COLOR;
        } else if (expression instanceof PixelLiteral) {
            return ExpressionType.PIXEL;
        } else if (expression instanceof PercentageLiteral) {
            return ExpressionType.PERCENTAGE;
        } else if (expression instanceof ScalarLiteral) {
            return ExpressionType.SCALAR;
        } else if (expression instanceof VariableReference) {
            VariableReference reference = (VariableReference) expression;
            for (int i = 0; i < globalVariablesList.getSize(); ++i) {
                if (globalVariablesList.get(i).containsKey(reference.name)) {
                    return globalVariablesList.get(i).get(reference.name);
                }
            }
        }
        return ExpressionType.UNDEFINED;
    }

    /**
     * Author: Raymond de Bruine
     * Date: 15-03-2021
     * Function: Return type of Expression
     * @param expression => Expression
     * @return => Type of Expression
     */
    private ExpressionType getExpressionType(Expression expression, IHANLinkedList<IHANLinkedList<HashMap<String, ExpressionType>>> localVarsList) {
        if (expression instanceof BoolLiteral) {
            return ExpressionType.BOOL;
        } else if (expression instanceof ColorLiteral) {
            return ExpressionType.COLOR;
        } else if (expression instanceof PixelLiteral) {
            return ExpressionType.PIXEL;
        } else if (expression instanceof PercentageLiteral) {
            return ExpressionType.PERCENTAGE;
        } else if (expression instanceof ScalarLiteral) {
            return ExpressionType.SCALAR;
        } else if (expression instanceof VariableReference) {
            VariableReference reference = (VariableReference) expression;
            for (int i = 0; i < localVarsList.getSize(); ++i) {
                for (int j = 0; j < localVarsList.get(i).getSize(); ++j) {
                    if (localVarsList.get(i).get(j).containsKey(reference.name)) {
                        return localVarsList.get(i).get(j).get(reference.name);
                    }
                }
            }
            for (int i = 0; i < globalVariablesList.getSize(); ++i) {
                if (globalVariablesList.get(i).containsKey(reference.name)) {
                    return globalVariablesList.get(i).get(reference.name);
                }
            }
        }
        return ExpressionType.UNDEFINED;
    }



    private void checkOperationValues(Operation operation, IHANLinkedList<IHANLinkedList<HashMap<String, ExpressionType>>> localVariableList) {
        try {
            getExpressionTypeOfOperation(operation, localVariableList);
        } catch (Exception e) {
            operation.setError(e.getMessage());
        }
    }

    private ExpressionType getExpressionTypeOfOperation(Operation operation, IHANLinkedList<IHANLinkedList<HashMap<String, ExpressionType>>> localVariableList) throws Exception {
        ExpressionType lhs = getExpressionType(operation.lhs, localVariableList);
        ExpressionType rhs = getExpressionType(operation.rhs, localVariableList);
        if (operation.rhs instanceof Operation) {
            rhs = getExpressionTypeOfOperation((Operation) operation.rhs, localVariableList);
        }

        if (lhs.equals(ExpressionType.COLOR) || lhs.equals(ExpressionType.BOOL)) {
            throw new Exception("Operations with " + lhs.toString() + " value are not allowed");
        }
        if (rhs.equals(ExpressionType.COLOR) || rhs.equals(ExpressionType.BOOL)) {
            throw new Exception("Operations with " + rhs.toString() + " value are not allowed");
        }

        if (operation instanceof AddOperation || operation instanceof SubtractOperation) {
            if (lhs != rhs) {
                throw new Exception("Add/Substract operations with a " + lhs.toString() + " and a " + rhs.toString() + " are not allowed");
            } else {
                return lhs;
            }
        }

        if (operation instanceof MultiplyOperation) {
            if (lhs.equals(ExpressionType.PERCENTAGE)) {
                if (rhs.equals(ExpressionType.PIXEL)) {
                    throw new Exception("Multiply operation with Percentage and Pixel values are not allowed");
                } else if (rhs.equals(ExpressionType.SCALAR)) {
                    return ExpressionType.PERCENTAGE;
                } else if (rhs.equals(ExpressionType.PERCENTAGE)) {
                    throw new Exception("One of the values in a multiply operation needs to be a Scalar");
                }
            } else if (lhs.equals(ExpressionType.PIXEL)) {
                if (rhs.equals(ExpressionType.PERCENTAGE)) {
                    throw new Exception("Multiply operation with Pixel and Percentage values are not allowed");
                } else if (rhs.equals(ExpressionType.SCALAR)) {
                    return ExpressionType.PIXEL;
                } else if (rhs.equals(ExpressionType.PIXEL)) {
                    throw new Exception("One of the values in a multiply operations needs to be a scalar");
                }
            } else if (lhs.equals(ExpressionType.SCALAR)) {
                if (rhs.equals(ExpressionType.PIXEL)) {
                    return ExpressionType.PIXEL;
                } else if (rhs.equals(ExpressionType.PERCENTAGE)) {
                    return ExpressionType.PERCENTAGE;
                } else if (rhs.equals(ExpressionType.SCALAR)) {
                    return ExpressionType.SCALAR;
                }
            }
        }
        return ExpressionType.UNDEFINED;
    }

    /**
     * Author: Raymond de Bruine
     * Date: 10-03-2021
     * Function: Business rules for using expresssion types with certain properties
     */
    private void setPropertyExpressionTypes() {
        propertyBusinessRulesList.put("background-color", new ArrayList<>());
        propertyBusinessRulesList.get("background-color").add(ExpressionType.COLOR);

        propertyBusinessRulesList.put("color", new ArrayList<>());
        propertyBusinessRulesList.get("color").add(ExpressionType.COLOR);

        propertyBusinessRulesList.put("width", new ArrayList<>());
        propertyBusinessRulesList.get("width").add(ExpressionType.PIXEL);
        propertyBusinessRulesList.get("width").add(ExpressionType.PERCENTAGE);

        propertyBusinessRulesList.put("height", new ArrayList<>());
        propertyBusinessRulesList.get("height").add(ExpressionType.PIXEL);
        propertyBusinessRulesList.get("height").add(ExpressionType.PERCENTAGE);
    }
}
