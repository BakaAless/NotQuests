/*
 * NotQuests - A Questing plugin for Minecraft Servers
 * Copyright (C) 2021 Alessio Gravili
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package rocks.gravili.notquests.paper.structs.conditions;

import cloud.commandframework.ArgumentDescription;
import cloud.commandframework.Command;
import cloud.commandframework.arguments.CommandArgument;
import cloud.commandframework.arguments.flags.CommandFlag;
import cloud.commandframework.arguments.standard.BooleanArgument;
import cloud.commandframework.arguments.standard.IntegerArgument;
import cloud.commandframework.arguments.standard.StringArgument;
import cloud.commandframework.paper.PaperCommandManager;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import redempt.crunch.CompiledExpression;
import redempt.crunch.Crunch;
import redempt.crunch.functional.EvaluationEnvironment;
import rocks.gravili.notquests.paper.NotQuests;
import rocks.gravili.notquests.paper.commands.arguments.variables.BooleanVariableValueArgument;
import rocks.gravili.notquests.paper.commands.arguments.variables.NumberVariableValueArgument;
import rocks.gravili.notquests.paper.structs.QuestPlayer;
import rocks.gravili.notquests.paper.structs.variables.Variable;
import rocks.gravili.notquests.paper.structs.variables.VariableDataType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BooleanCondition extends Condition {

    private String variableName;
    private String operator;
    private String expression;

    private HashMap<String, String> additionalStringArguments;
    private HashMap<String, String> additionalNumberArguments;
    private HashMap<String, Boolean> additionalBooleanArguments;

    private CompiledExpression exp;
    private final EvaluationEnvironment env = new EvaluationEnvironment();
    private int variableCounter = 0;
    Variable<?> cachedVariable = null;
    private QuestPlayer questPlayerToEvaluate = null;



    public final String getOperator(){
        return operator;
    }
    public void setOperator(final String operator){
        this.operator = operator;
    }

    public final String getVariableName(){
        return variableName;
    }

    public void setVariableName(final String variableName){
        this.variableName = variableName;
    }

    public void setExpression(final String expression){
        this.expression = expression;
    }

    public final String getExpression(){
        return expression;
    }


    public BooleanCondition(NotQuests main) {
        super(main);
        additionalStringArguments = new HashMap<>();
        additionalNumberArguments = new HashMap<>();
        additionalBooleanArguments = new HashMap<>();
    }


    /*public final boolean evaluateExpression(final QuestPlayer questPlayer){
        boolean booleanRequirement = false;

        try{
            booleanRequirement = Boolean.parseBoolean(getExpression());
        }catch (Exception e){
            //Might be another variable
            for(final String otherVariableName : main.getVariablesManager().getVariableIdentifiers()){
                if(!getExpression().equalsIgnoreCase(otherVariableName)){
                    continue;
                }
                final Variable<?> otherVariable = main.getVariablesManager().getVariableFromString(otherVariableName);
                if(otherVariable == null || otherVariable.getVariableDataType() != VariableDataType.BOOLEAN){
                    main.getLogManager().debug("Null variable: <highlight>" + otherVariableName);
                    continue;
                }
                if(otherVariable.getValue(questPlayer.getPlayer(), questPlayer) instanceof Boolean bool){
                    booleanRequirement = bool;
                    break;
                }
            }
        }
        return booleanRequirement;
    }*/


    public String getExpressionAndGenerateEnv(String expressions){
        boolean foundOne = false;
        for(String variableString : main.getVariablesManager().getVariableIdentifiers()){
            if(!expressions.contains(variableString)){
                continue;
            }
            Variable<?> variable = main.getVariablesManager().getVariableFromString(variableString);
            if(variable == null || (variable.getVariableDataType() != VariableDataType.NUMBER && variable.getVariableDataType() != VariableDataType.BOOLEAN)){
                main.getLogManager().debug("Null variable: <highlight>" + variableString);
                continue;
            }

            //Extra Arguments:
            if(expressions.contains(variableString + "(")){
                foundOne = true;
                String everythingAfterBracket = expressions.substring(expressions.indexOf(variableString+"(") +  variableString.length()+1 );
                String insideBracket = everythingAfterBracket.substring(0, everythingAfterBracket.indexOf(")"));
                main.getLogManager().debug("Inside Bracket: " + insideBracket);
                String[] extraArguments = insideBracket.split(",");
                for(String extraArgument : extraArguments){
                    main.getLogManager().debug("Extra: " + extraArgument);
                    if(extraArgument.startsWith("--")){
                        variable.addAdditionalBooleanArgument(extraArgument.replace("--", ""), true);
                        main.getLogManager().debug("AddBoolFlag: " + extraArgument.replace("--", ""));
                    }else{
                        String[] split = extraArgument.split(":");
                        String key = split[0];
                        String value = split[1];
                        for(StringArgument<CommandSender> stringArgument : variable.getRequiredStrings()){
                            if(stringArgument.getName().equalsIgnoreCase(key)){
                                variable.addAdditionalStringArgument(key, value);
                                main.getLogManager().debug("AddString: " + key + " val: " + value);
                            }
                        }
                        for(NumberVariableValueArgument<CommandSender> numberVariableValueArgument : variable.getRequiredNumbers()){
                            if(numberVariableValueArgument.getName().equalsIgnoreCase(key)){
                                variable.addAdditionalNumberArgument(key, value);
                                main.getLogManager().debug("AddNumb: " + key + " val: " + value);
                            }
                        }
                        for(BooleanArgument<CommandSender> booleanArgument : variable.getRequiredBooleans()){
                            if(booleanArgument.getName().equalsIgnoreCase(key)){
                                variable.addAdditionalBooleanArgument(key, Boolean.parseBoolean(value));
                                main.getLogManager().debug("AddBool: " + key + " val: " + value);
                            }
                        }
                    }
                }

                variableString = variableString+"(" + insideBracket + ")"; //For the replacing with the actual number below
            }


            variableCounter++;
            expressions = expressions.replace(variableString, "var" + variableCounter);
            env.addLazyVariable("var" + variableCounter, () -> {
                final Object valueObject = variable.getValue(questPlayerToEvaluate.getPlayer(), questPlayerToEvaluate);
                if(valueObject instanceof final Number n){
                    return n.doubleValue();
                }else if(valueObject instanceof final Boolean b) {
                    return b ? 1 : 0;
                }
                return 0;
            });
        }
        if(!foundOne){
            return expressions;
        }

        return getExpressionAndGenerateEnv(expressions);
    }

    public void initializeExpressionAndCachedVariable(){
        if(exp == null){
            String expression = getExpressionAndGenerateEnv(getExpression());
            exp = Crunch.compileExpression(expression, env);
            cachedVariable = main.getVariablesManager().getVariableFromString(variableName);
        }
    }

    @Override
    public String checkInternally(final QuestPlayer questPlayer) {
        this.questPlayerToEvaluate = questPlayer;
        initializeExpressionAndCachedVariable();




        if(cachedVariable == null){
            return main.getLanguageManager().getString("chat.conditions.boolean.variable-not-found", questPlayer.getPlayer(), questPlayer, Map.of(
                    "%VARIABLENAME%", variableName
            ));
        }

        final boolean booleanRequirement = exp.evaluate() >= 0.98d;


        if(additionalStringArguments != null && !additionalStringArguments.isEmpty()){
            cachedVariable.setAdditionalStringArguments(additionalStringArguments);
        }
        if(additionalNumberArguments != null && !additionalNumberArguments.isEmpty()){
            cachedVariable.setAdditionalNumberArguments(additionalNumberArguments);
        }
        if(additionalBooleanArguments != null && !additionalBooleanArguments.isEmpty()){
            cachedVariable.setAdditionalBooleanArguments(additionalBooleanArguments);
        }

        Object value = cachedVariable.getValue(questPlayer.getPlayer(), questPlayer);

        if(getOperator().equalsIgnoreCase("equals")){
            if(value instanceof Boolean bool){
                if (booleanRequirement != bool) {
                    return main.getLanguageManager().getString("chat.conditions.boolean.not-fulfilled", questPlayer.getPlayer(), questPlayer, Map.of(
                            "%OPERATOR%", getOperator(),
                            "%BOOLEANREQUIREMENT%", ""+booleanRequirement,
                            "%VARIABLESINGULAR%", cachedVariable.getSingular(),
                            "%VARIABLEPLURAL%", cachedVariable.getPlural()
                    ));
                }
            }else{
                if (booleanRequirement != (boolean)value) {
                    return main.getLanguageManager().getString("chat.conditions.boolean.not-fulfilled", questPlayer.getPlayer(), questPlayer, Map.of(
                            "%OPERATOR%", getOperator(),
                            "%BOOLEANREQUIREMENT%", ""+booleanRequirement,
                            "%VARIABLESINGULAR%", cachedVariable.getSingular(),
                            "%VARIABLEPLURAL%", cachedVariable.getPlural()
                    ));
                }
            }
        }else{
            return main.getLanguageManager().getString("chat.conditions.boolean.wrong-operator", questPlayer.getPlayer(), questPlayer, Map.of(
                    "%OPERATOR%", getOperator()
            ));
        }

        return "";
    }

    @Override
    public void save(FileConfiguration configuration, final String initialPath) {
        configuration.set(initialPath + ".specifics.variableName", getVariableName());
        configuration.set(initialPath + ".specifics.operator", getOperator());
        configuration.set(initialPath + ".specifics.expression", getExpression());

        for(final String key : additionalStringArguments.keySet()){
            configuration.set(initialPath + ".specifics.additionalStrings." + key, additionalStringArguments.get(key));
        }
        for(final String key : additionalNumberArguments.keySet()){
            configuration.set(initialPath + ".specifics.additionalNumbers." + key, additionalNumberArguments.get(key));
        }
        for(final String key : additionalBooleanArguments.keySet()){
            configuration.set(initialPath + ".specifics.additionalBooleans." + key, additionalBooleanArguments.get(key));
        }
    }

    @Override
    public void load(FileConfiguration configuration, String initialPath) {
        this.variableName = configuration.getString(initialPath + ".specifics.variableName");
        this.operator = configuration.getString(initialPath + ".specifics.operator", "");
        this.expression = configuration.getString(initialPath + ".specifics.expression", "");

        final ConfigurationSection additionalStringsConfigurationSection = configuration.getConfigurationSection(initialPath + ".specifics.additionalStrings");
        if (additionalStringsConfigurationSection != null) {
            for (String key : additionalStringsConfigurationSection.getKeys(false)) {
                additionalStringArguments.put(key, configuration.getString(initialPath + ".specifics.additionalStrings." + key, ""));
            }
        }

        final ConfigurationSection additionalIntegersConfigurationSection = configuration.getConfigurationSection(initialPath + ".specifics.additionalNumbers");
        if (additionalIntegersConfigurationSection != null) {
            for (String key : additionalIntegersConfigurationSection.getKeys(false)) {
                additionalNumberArguments.put(key, configuration.getString(initialPath + ".specifics.additionalNumbers." + key, "0"));
            }
        }

        final ConfigurationSection additionalBooleansConfigurationSection = configuration.getConfigurationSection(initialPath + ".specifics.additionalBooleans");
        if (additionalBooleansConfigurationSection != null) {
            for (String key : additionalBooleansConfigurationSection.getKeys(false)) {
                additionalBooleanArguments.put(key, configuration.getBoolean(initialPath + ".specifics.additionalBooleans." + key, false));
            }
        }
        initializeExpressionAndCachedVariable();
    }

    @Override
    public void deserializeFromSingleLineString(ArrayList<String> arguments) {
        this.variableName = arguments.get(0);

        this.operator = arguments.get(1);
        setExpression(arguments.get(2));

        if(arguments.size() >= 4){

            Variable<?> variable = main.getVariablesManager().getVariableFromString(variableName);
            if(variable == null || !variable.isCanSetValue() || variable.getVariableDataType() != VariableDataType.BOOLEAN){
                return;
            }

            int counter = 0;
            int counterStrings = 0;
            int counterNumbers = 0;
            int counterBooleans = 0;
            int counterBooleanFlags = 0;

            for (String argument : arguments){
                counter++;
                if(counter >= 4){
                    if(variable.getRequiredStrings().size() > counterStrings){
                        additionalStringArguments.put(variable.getRequiredStrings().get(counter-4).getName(), argument);
                        counterStrings++;
                    } else if(variable.getRequiredNumbers().size() > counterNumbers){
                        additionalNumberArguments.put(variable.getRequiredNumbers().get(counter-4).getName(), argument);
                        counterNumbers++;
                    } else if(variable.getRequiredBooleans().size()  > counterBooleans){
                        additionalBooleanArguments.put(variable.getRequiredBooleans().get(counter-4).getName(), Boolean.parseBoolean(argument));
                        counterBooleans++;
                    } else if(variable.getRequiredBooleanFlags().size()  > counterBooleanFlags){
                        additionalBooleanArguments.put(variable.getRequiredBooleanFlags().get(counter-4).getName(), Boolean.parseBoolean(argument));
                        counterBooleanFlags++;
                    }
                }
            }
        }
        initializeExpressionAndCachedVariable();
    }

    @Override
    public String getConditionDescriptionInternally(Player player, Object... objects) {
        //description += "\n<GRAY>--- Will quest points be deducted?: No";
        this.questPlayerToEvaluate = main.getQuestPlayerManager().getOrCreateQuestPlayer(player.getUniqueId());
        initializeExpressionAndCachedVariable();
        final boolean booleanRequirement = exp.evaluate() >= 0.98d;


        if(getOperator().equalsIgnoreCase("equals")){
            return "<GRAY>-- " + variableName + " needs to be " + booleanRequirement + "</GRAY>";
        }
        return "<GRAY>-- " + variableName + " needed: " + booleanRequirement + "</GRAY>";
    }



    public static void handleCommands(NotQuests main, PaperCommandManager<CommandSender> manager, Command.Builder<CommandSender> builder, ConditionFor conditionFor) {
        for(String variableString : main.getVariablesManager().getVariableIdentifiers()){

            Variable<?> variable = main.getVariablesManager().getVariableFromString(variableString);

            if(variable == null || variable.getVariableDataType() != VariableDataType.BOOLEAN){
                continue;
            }

            if(main.getVariablesManager().alreadyFullRegisteredVariables.contains(variableString)){
                continue;
            }

            main.getLogManager().info("Registering boolean condition: <highlight>" + variableString);


            manager.command(main.getVariablesManager().registerVariableCommands(variableString, builder)
                    .argument(StringArgument.<CommandSender>newBuilder("operator").withSuggestionsProvider((context, lastString) -> {
                        ArrayList<String> completions = new ArrayList<>();
                        completions.add("equals");


                        final List<String> allArgs = context.getRawInput();
                        main.getUtilManager().sendFancyCommandCompletion(context.getSender(), allArgs.toArray(new String[0]), "[Comparison Operator]", "[...]");

                        return completions;
                    }).build(), ArgumentDescription.of("Comparison operator."))
                    .argument(BooleanVariableValueArgument.newBuilder("expression", main, variable), ArgumentDescription.of("Expression"))
                    .handler((context) -> {

                        final String expression = context.get("expression");
                        final String operator = context.get("operator");

                        BooleanCondition booleanCondition = new BooleanCondition(main);
                        booleanCondition.setExpression(expression);
                        booleanCondition.setOperator(operator);
                        booleanCondition.setVariableName(variable.getVariableType());


                        HashMap<String, String> additionalStringArguments = new HashMap<>();
                        for(StringArgument<CommandSender> stringArgument : variable.getRequiredStrings()){
                            additionalStringArguments.put(stringArgument.getName(), context.get(stringArgument.getName()));
                        }
                        booleanCondition.setAdditionalStringArguments(additionalStringArguments);

                        HashMap<String, String> additionalNumberArguments = new HashMap<>();
                        for(NumberVariableValueArgument<CommandSender> numberVariableValueArgument : variable.getRequiredNumbers()){
                            additionalNumberArguments.put(numberVariableValueArgument.getName(), context.get(numberVariableValueArgument.getName()));
                        }
                        booleanCondition.setAdditionalNumberArguments(additionalNumberArguments);

                        HashMap<String, Boolean> additionalBooleanArguments = new HashMap<>();
                        for(BooleanArgument<CommandSender> booleanArgument : variable.getRequiredBooleans()){
                            additionalBooleanArguments.put(booleanArgument.getName(), context.get(booleanArgument.getName()));
                        }
                        for(CommandFlag<?> commandFlag : variable.getRequiredBooleanFlags()){
                            additionalBooleanArguments.put(commandFlag.getName(), context.flags().isPresent(commandFlag.getName()));
                        }
                        booleanCondition.setAdditionalBooleanArguments(additionalBooleanArguments);

                        booleanCondition.initializeExpressionAndCachedVariable();

                        main.getConditionsManager().addCondition(booleanCondition, context);
                    })
            );


        }


    }

    private void setAdditionalStringArguments(HashMap<String, String> additionalStringArguments) {
        this.additionalStringArguments = additionalStringArguments;
    }
    private void setAdditionalNumberArguments(HashMap<String, String> additionalNumberArguments) {
        this.additionalNumberArguments = additionalNumberArguments;
    }
    private void setAdditionalBooleanArguments(HashMap<String, Boolean> additionalBooleanArguments) {
        this.additionalBooleanArguments = additionalBooleanArguments;
    }

}
