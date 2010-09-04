.assembly extern mscorlib {}
.assembly 'fichero' {}

.class 'Single' extends [mscorlib]System.Object
{
	.method public specialname rtspecialname instance void .ctor() cil managed
	{
		.maxstack 1
		ldarg.0
		call instance void [mscorlib]System.Object::.ctor()
		ret
	}
	.method static public void Main () cil managed
	{
		.entrypoint
		.locals(bool, int32, float64)
		.maxstack 2
		ldloc 0
		call void [mscorlib]System.Console::WriteLine(int32)
		ldloc 1
		call void [mscorlib]System.Console::WriteLine(int32)
		ldloc 2
		call void [mscorlib]System.Console::WriteLine(float64)
		ret
	}
}
