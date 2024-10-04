# Intelligent Systems Through Sukoban

Checkbox

- [x] base1.txt
- [x] base2.txt
- [x] base3.txt
- [x] base4.txt
- [x] twoboxes1.txt
- [ ] twoboxes2.txt
- [ ] twoboxes3.txt
- [ ] threeboxes1.txt
- [x] threeboxes2.txt
- [ ] threeboxes3.txt
- [x] fourboxes1.txt
- [ ] fourboxes2.txt 
- [ ] fourboxes3.txt    (TLE)
- [x] fiveboxes1.txt
- [ ] fiveboxes2.txt    (TLE)
- [x] fiveboxes3.txt
- [ ] original1.txt     (TLE)
- [ ] original2.txt
- [ ] original3.txt     


This is actually pretty hard.

Note: if class instantiation becomes a significant overhead, we might refactor our code to use static methods instead (i.e., because OOP is very inefficient for these types of applications, we might resort to procedural idioms if our algo is still shit).







So... refactor the StateFactory class... too much redundancy.


Another note.. the player is currently an implicit concept... should we turn it into its own class? idfk

"unless it moved a crate, then undoing its previous movve is meaningless"