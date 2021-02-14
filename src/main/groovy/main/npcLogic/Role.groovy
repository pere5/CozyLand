package main.npcLogic

abstract class Role {

    String id
    List<Rule> rules = []
    Tribe tribe

    Role(String id, Tribe tribe) {
        this.id = id
        this.rules = constructRuleList()
        this.tribe = tribe
    }

    abstract List<Rule> constructRuleList()
}
