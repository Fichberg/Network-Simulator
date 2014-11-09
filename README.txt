- O usuario deve entrar com um arquivo que tenha os hosts e os routers numerados, EM ORDEM, de 0 a n. Assim sendo, uma entrada válida para n = 4 hosts, é:

set h0 [$simulator host]
set h1 [$simulator host]
set h2 [$simulator host]
set h3 [$simulator host]

E uma entrada inválida é:

set h2 [$simulator host]
set h1 [$simulator host]
set h3 [$simulator host]
set h4 [$simulator host]