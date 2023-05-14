grammar g;


prog: (operation)+EOF
;

operation: (createTable | createIndex | insert | update | delete | selection)
;
createTable: 'create table'  STRING  '('  columns  ')';
createIndex: 'create index'  STRING  'on' STRING ;
insert: 'insert into' STRING  '(' values')';
update: 'update' STRING  'set' STRING  '='  VALUE;
delete: 'delete from' STRING  condition;
selection: 'select' columns 'from' STRING  condition;
columns: STRING | STRING ',' columns;
values: INT| STRING | VALUE ',' values;
condition: 'where' statement | ;
statement: STRING operator VALUE ;
operator: '=' | '>'| '<'|'>=' | '<=';

INT: [1-9]*;
STRING : [a-z]*;
WS  :   [ \t]+ -> skip ;
NEWLINE:'\r'? '\n' ;






