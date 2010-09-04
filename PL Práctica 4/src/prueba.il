.assembly extern mscorlib {}
.assembly 'prueba' {}

.class 'Hola' extends [mscorlib]System.Object
{
	.method public specialname rtspecialname instance void .ctor() cil managed
	{
		.maxstack 1
		ldarg.0
		call instance void [mscorlib]System.Object::.ctor()
		ret
	}

	.method static public void main () cil managed
	{
		.entrypoint
		.locals(int32[])
		.maxstack 999
		ldc.i4 31500000
		newarr [mscorlib]System.Int32
		stloc 0
		ldloc 0
		ldc.i4 56
		conv.i4
		ldc.i4 56
		conv.i4
		ldc.i4 50
		blt L1
		ldstr "Se ha salido del indice."
		call void [mscorlib]System.Console::Write(string)
		throw
		L1:
		ldc.i4 100
		mul
		ldc.i4 20
		conv.i4
		add
		ldc.i4 20
		conv.i4
		ldc.i4 100
		blt L2
		ldstr "Se ha salido del indice."
		call void [mscorlib]System.Console::Write(string)
		throw
		L2:
		ldc.i4 70
		mul
		ldc.i4 30
		conv.i4
		add
		ldc.i4 30
		conv.i4
		ldc.i4 70
		blt L3
		ldstr "Se ha salido del indice."
		call void [mscorlib]System.Console::Write(string)
		throw
		L3:
		ldc.i4 90
		mul
		ldc.i4 40
		conv.i4
		add
		ldc.i4 40
		conv.i4
		ldc.i4 90
		blt L4
		ldstr "Se ha salido del indice."
		call void [mscorlib]System.Console::Write(string)
		throw
		L4:
		ldc.i4 100
		conv.i4
		stelem.i4
		ret
	}
}
