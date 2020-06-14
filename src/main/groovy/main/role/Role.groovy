package main.role

import main.model.Villager
import main.rule.Rule

import java.awt.Color

abstract class Role {

    String id
    List<Rule> rules = []
    List<Villager> followers = []
    Villager chief
    Color tribeColor
}
