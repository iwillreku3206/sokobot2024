# Intelligent Systems Through Sukoban

Checkbox

- [x] base1.txt
- [x] base2.txt
- [x] base3.txt
- [ ] base4.txt
- [x] twoboxes1.txt
- [ ] twoboxes2.txt
- [ ] twoboxes3.txt
- [ ] threeboxes1.txt
- [ ] threeboxes2.txt   (returns empty string)
- [ ] threeboxes3.txt
- [x] fourboxes1.txt
- [ ] fourboxes2.txt 
- [ ] fourboxes3.txt    (TLE)
- [ ] fiveboxes1.txt    (returns empty string)
- [ ] fiveboxes2.txt
- [ ] fiveboxes3.txt
- [ ] original1.txt
- [ ] original2.txt
- [ ] original3.txt     (TLE)


This is actually pretty hard.

Note: if class instantiation becomes a significant overhead, we might refactor our code to use static methods instead (i.e., because OOP is very inefficient for these types of applications, we might resort to procedural idioms if our algo is still shit).







So... refactor the StateFactory class... too much redundancy.


Another note.. the player is currently an implicit concept... should we turn it into its own class? idfk

"unless it moved a crate, then undoing its previous movve is meaningless"