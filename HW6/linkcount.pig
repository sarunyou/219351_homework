items = LOAD './web-Google.txt' USING PigStorage('\t') AS (fromNode: int, toNode: int);
grouped = GROUP records BY toNode;
results = foreach grouped generate group, COUNT(items);
STORE results INTO './linkcount';
