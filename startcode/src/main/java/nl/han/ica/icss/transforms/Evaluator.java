package nl.han.ica.icss.transforms;

import nl.han.ica.datastructures.HANLinkedList;
import nl.han.ica.datastructures.IHANLinkedList;
import nl.han.ica.icss.ast.*;
import nl.han.ica.icss.ast.literals.*;

import java.util.ArrayList;
import java.util.HashMap;

public class Evaluator implements Transform {

    private static IHANLinkedList<HashMap<String, Literal>> globalVariableValues;

    public Evaluator() {
        globalVariableValues = new HANLinkedList<>();
    }

    @Override
    public void apply(AST ast) {
        for (ASTNode parent: ast.root.getChildren()) {
            if (parent instanceof VariableAssignment) {
                if (this.checkIfVariableIsNew(((VariableAssignment) parent).name)) {
                    this.setVariableInList((VariableAssignment) parent, globalVariableValues);
                } else {
                    this.updateGlobalVariableInList((VariableAssignment) parent);
                }
            } else if (parent instanceof Stylerule) {
                IHANLinkedList<IHANLinkedList<HashMap<String, Literal>>> localVariableValues = new HANLinkedList<>();
                localVariableValues.addFirst(new HANLinkedList<>());
                for (ASTNode child: parent.getChildren()) {
                    this.checkTreeNode(parent, child, localVariableValues);
                }
            }
        }
        System.out.println(ast.root.toString());
    }

    private void checkTreeNode(ASTNode parent, ASTNode node, IHANLinkedList<IHANLinkedList<HashMap<String, Literal>>> localVariableList) {
        if (node instanceof VariableAssignment) {
            if (this.checkIfVariableIsNew(((VariableAssignment) node).name, localVariableList)) {
                this.setVariableInList((VariableAssignment) node, localVariableList.getFirst());
            } else {
                updateVariableInList((VariableAssignment) node, localVariableList);
            }
        } else if (node instanceof Declaration) {
            if (((Declaration) node).expression instanceof VariableReference) {
                try {
                    ((Declaration) node).expression =
                            this.replaceVariableReference((VariableReference) ((Declaration) node).expression, localVariableList);
                } catch (Exception e) {
                    ((Declaration) node).expression.setError(e.getMessage());
                }
            }
        } else if (node instanceof IfClause) {
            System.out.println("Hooiii een if statement");
            if (((IfClause) node).conditionalExpression instanceof VariableReference) {
                try {
                    ((IfClause) node).conditionalExpression = this.replaceVariableReference((VariableReference) ((IfClause) node).conditionalExpression, localVariableList);
                } catch (Exception e) {
                    ((IfClause) node).conditionalExpression.setError(e.getMessage());
                }
            }
            System.out.println(((IfClause) node).conditionalExpression);
            ArrayList<ASTNode> conditionalBody = this.evaluateConditionalExpression((IfClause) node);
            localVariableList.addFirst(new HANLinkedList<>());
            for (ASTNode bodyChild: conditionalBody) {
                checkTreeNode(node, bodyChild, localVariableList);
                parent.addChild(bodyChild);
            }
            localVariableList.removeFirst();
            parent.removeChild(node);
        }
        for (ASTNode child: node.getChildren()) {
            checkTreeNode(node, child, localVariableList);
        }
    }

    private ArrayList<ASTNode> evaluateConditionalExpression(IfClause ifClause) {
        if (((BoolLiteral) ifClause.conditionalExpression).value) {
            return ifClause.body;
        } else if (!((BoolLiteral) ifClause.conditionalExpression).value && ifClause.elseClause.body != null) {
            return ifClause.elseClause.body;
        } else {
            return new ArrayList<>();
        }
    }

    /**
     * Author: Raymond de Bruine
     * Date: 19-03-2021
     * Search for the variable reference and returns the Literal.
     * @param node => The variable reference
     * @param localVariableList => List with local variables
     * @return => Literal of the expression
     * @throws Exception => When variable reference is not found in local and global variable lists
     */
    private Literal replaceVariableReference(VariableReference node, IHANLinkedList<IHANLinkedList<HashMap<String, Literal>>>
            localVariableList) throws Exception {
        for (int i = 0; i < localVariableList.getSize(); ++i) {
            for (int j = 0; j < localVariableList.get(i).getSize(); ++j) {
                if (localVariableList.get(i).get(j).containsKey(node.name)) {
                    return localVariableList.get(i).get(j).get(node.name);
                }
            }
        }
        for (int i = 0; i < globalVariableValues.getSize(); ++i) {
            if (globalVariableValues.get(i).containsKey(node.name)) {
                return globalVariableValues.get(i).get(node.name);
            }
        }
        throw new Exception("No variable found with reference " + node.name);
    }

    /**
     * Author: Raymond de Bruine
     * Date: 16-03-2021
     * Function: Update a global variable in the list
     * @param node => The updated variable node
     */
    private void updateGlobalVariableInList(VariableAssignment node) {
        for (int i = 0; i < globalVariableValues.getSize(); ++i) {
            if (globalVariableValues.get(i).containsKey(node.name.name)) {
                globalVariableValues.get(i).put(node.name.name, getLiteral(node.expression));
                return;
            }
        }
    }

    private void updateVariableInList(VariableAssignment node, IHANLinkedList<IHANLinkedList<HashMap<String, Literal>>> variableList) {
        for (int i = 0; i < variableList.getSize(); ++i) {
            for (int j = 0; i < variableList.get(i).getSize(); ++j) {
                if (variableList.get(i).get(j).containsKey(node.name.name)) {
                    variableList.get(i).get(j).put(node.name.name, getLiteral(node.expression));
                    return;
                }
            }
        }
        updateGlobalVariableInList(node);
    }

    /**
     * Author: Raymond de Bruine
     * Date: 16-03-2021
     * Function: Set new variable in list
     * @param variable => The new variable assignment
     * @param variableList => List with variables where the new variable needs to be stored
     */
    private void setVariableInList(VariableAssignment variable, IHANLinkedList<HashMap<String, Literal>> variableList) {
        HashMap<String, Literal> variableMap = new HashMap<>();
        variableMap.put(variable.name.name, getLiteral(variable.expression));
        variableList.addFirst(variableMap);
    }

    /**
     * Author: Raymond de Bruine
     * Date: 16-03-2021
     * Function: Get literal from expression
     * @param expression => The expression node
     * @return => Literal type
     */
    private Literal getLiteral(Expression expression) {
        if (expression instanceof BoolLiteral) {
            return (BoolLiteral) expression;
        } else if (expression instanceof ColorLiteral) {
            return (ColorLiteral) expression;
        } else if (expression instanceof PercentageLiteral) {
            return (PercentageLiteral) expression;
        } else if (expression instanceof PixelLiteral) {
            return (PixelLiteral) expression;
        } else if (expression instanceof ScalarLiteral) {
            return (ScalarLiteral) expression;
        } else if (expression instanceof VariableReference) {
            VariableReference reference = (VariableReference) expression;
            for (int i = 0; i < globalVariableValues.getSize(); ++i) {
                if (globalVariableValues.get(i).containsKey(reference.name)) {
                    return globalVariableValues.get(i).get(reference.name);
                }
            }
        }
        return null;
    }

    /**
     * Author: Raymond de Bruine
     * Date: 16-03-2021
     * @param variable => Variable reference to be checked
     * @return => True if variable not exists in list
     */
    private boolean checkIfVariableIsNew(VariableReference variable) {
        return !variableMapKeyExists(variable.name, new HANLinkedList<>());
    }

    /**
     * Author: Raymond de Bruine
     * Date: 16-03-2021
     * @param variable => Variable reference to be checked
     * @param localVariableValuesList => List with local variables
     * @return => true if variable exists in list.
     */
    private boolean checkIfVariableIsNew(VariableReference variable, IHANLinkedList<IHANLinkedList<HashMap<String, Literal>>>
            localVariableValuesList) {
        for (int i = 0; i < localVariableValuesList.getSize(); ++i) {
            if (variableMapKeyExists(variable.name, localVariableValuesList.get(i))) {
                return false;
            }
        }
        return !variableMapKeyExists(variable.name, globalVariableValues);
    }

    /**
     * Author: Raymond de Bruine
     * Date: 16-03-2021
     * @param name => Name of key
     * @param variableList => LinkedList with map of variables
     * @return true when key exist in map of a list
     */
    private boolean variableMapKeyExists(String name, IHANLinkedList<HashMap<String, Literal>> variableList) {
        for (int i = 0; i < variableList.getSize(); ++i) {
            if (variableList.get(i).containsKey(name)) {
                return true;
            }
        }
        return false;
    }

    
}
