grammar g;

prog: (createTable | createIndex | insert | update | delete | selection )+EOF;
createTable: 'create table' STRING  '('  columns  ')';
createIndex: 'create index' STRING 'on' STRING '(' columns ')';
insert: 'insert into' STRING '(' columns ')' 'values' '(' values ')';
update: 'update' STRING 'set' condition 'where' condition;
delete: 'delete from' STRING 'where' condition;
condition: statement | statement LOGICAL_OP statement ;
selection: 'select' '*' 'from' STRING 'where' condition;
columns: STRING | STRING ',' columns;
values: object | object ',' values;
statement: STRING OPERATOR object ;
object : INT | DOUBLE | STRING | DATE ;

INT: '0' | [1-9][0-9]*;
DATE: [0-9]{4}'-'[0-9]{2}'-'[0-9]{2};
OPERATOR : '=' | '>' | '<' | '>=' | '<=';
LOGICAL_OP : 'AND' | 'OR' | 'XOR' | ',';
DOUBLE : [0-9]+ '.' [0-9]+ ;
STRING : [a-zA-Z]*;
WS : [ \t\n\r]+ -> skip ;
NEWLINE:'\r'? '\n' ;








