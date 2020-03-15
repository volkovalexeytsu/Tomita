#encoding "utf-8"

#GRAMMAR_ROOT M 

Num -> AnyWord<wff=/[1-9]?[0-9]{1,6}/>;

Exp -> 'корень' 'из'  Exp  interp (Sqrt.Recursion) 'конец' 'подкорня';
Exp -> Num interp(Sqrt.Value);

M -> Exp;
