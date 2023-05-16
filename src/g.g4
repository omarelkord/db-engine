grammar g;


prog: (operation)+EOF
;

operation: (createTable | createIndex | insert | update | delete | selection);
createTable: 'create table'  STRING  '('  columns  ')';
createIndex: 'create index'  STRING  'on' STRING ;
insert: 'insert into' STRING  '(' values')';
update: 'update' STRING  'set' updatecolumns condition;
delete: 'delete from' STRING  condition;
selection: 'select' columns 'from' STRING  condition;
columns: STRING | STRING ',' columns;
updatecolumns: STRING '=' VALUE | STRING '=' VALUE ',' updatecolumns;
values: INT| STRING | VALUE ',' values;
condition: 'where' statement | ;
statement: STRING operator VALUE ;
operator: '=' | '>'| '<'|'>=' | '<=';

INT: [1-9]*;
DOUBLE :[0-9]+ '.' [0-9]+ ;
STRING : [a-z]*;
WS  :   [ \t]+ -> skip ;
NEWLINE:'\r'? '\n' ;