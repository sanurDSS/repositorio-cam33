.assembly extern mscorlib {}
.assembly 'autocorrector/jose-001' {}

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
		.locals(int32, int32, int32[], int32[], int32[], float64[], float64, int32, int32, int32[], int32, int32, int32, int32, int32, int32, float64)
		.maxstack 10
		call string [mscorlib]System.Console::ReadLine()
		call int32 [mscorlib]System.Int32::Parse(string)
		conv.i4
		stloc 0
		ldc.i4 1
		ldloc 0
		ldc.i4 1
		add
		add
		ldc.i4 1
		ldc.i4 2
		add
		add
		conv.i4
		stloc 1
		ldloc 0
		call void [mscorlib]System.Console::WriteLine(int32)
		ldloc 1
		call void [mscorlib]System.Console::WriteLine(int32)
		call string [mscorlib]System.Console::ReadLine()
		call bool [mscorlib]System.Boolean::Parse(string)
		conv.i4
		stloc 7
		ldloc 7
		ldc.i4.1
		xor
		call void [mscorlib]System.Console::WriteLine(bool)
		ldloc 7
		call void [mscorlib]System.Console::WriteLine(bool)
		ldloc 7
		ldc.i4.1
		xor
		ldc.i4.1
		xor
		ldc.i4.1
		xor
		ldc.i4.1
		xor
		ldc.i4.1
		xor
		conv.i4
		stloc 8
		ldloc 8
		call void [mscorlib]System.Console::WriteLine(bool)
		ldc.i4 25
		newarr [mscorlib]System.Int32
		stloc 9
		ldc.i4 0
		conv.i4
		stloc 1
		ldc.i4 10
		ldc.i4 1
		ldc.i4 1
		ldc.i4 1
		ldc.i4 1
		ldc.i4 1
		ldc.i4 1
		ldc.i4 1
		ldc.i4 1
		ldc.i4 1
		sub
		sub
		sub
		sub
		sub
		sub
		sub
		sub
		sub
		ldc.i4 5
		sub
		conv.i4
		stloc 10
		L17:
		ldloc 10
		conv.r8
		ldc.i4 0
		conv.r8
		bge L1
		ldc.i4.0
		br L2
		L1:
		ldc.i4.1
		L2:
		ldloc 10
		conv.r8
		ldc.i4 0
		conv.r8
		bge L3
		ldc.i4.0
		br L4
		L3:
		ldc.i4.1
		L4:
		or
		ldloc 10
		conv.r8
		ldloc 1
		ldloc 1
		ldc.i4.1
		add
		stloc 1
		conv.r8
		bge L5
		ldc.i4.0
		br L6
		L5:
		ldc.i4.1
		L6:
		or
		ldc.i4.1
		beq L18
		br L19
		L18:
		ldloc 10
		call void [mscorlib]System.Console::WriteLine(int32)
		ldc.i4 0
		conv.i4
		stloc 11
		L9:
		ldloc 11
		conv.r8
		ldc.i4 5
		conv.r8
		blt L7
		ldc.i4.0
		br L8
		L7:
		ldc.i4.1
		L8:
		ldc.i4.1
		beq L10
		br L11
		L10:
		ldloc 9
		ldloc 10
		conv.i4
		ldc.i4 5
		mul
		ldloc 11
		conv.i4
		add
		ldc.i4 1
		conv.i4
		stelem.i4
		ldloc 11
		ldloc 11
		ldc.i4.1
		add
		stloc 11
		pop
		br L9
		L11:
		ldc.i4 0
		conv.i4
		stloc 12
		L14:
		ldloc 12
		conv.r8
		ldc.i4 5
		conv.r8
		blt L12
		ldc.i4.0
		br L13
		L12:
		ldc.i4.1
		L13:
		ldc.i4.1
		beq L15
		br L16
		L15:
		ldloc 9
		ldloc 10
		conv.i4
		ldc.i4 5
		mul
		ldloc 12
		conv.i4
		add
		ldc.i4 0
		conv.i4
		stelem.i4
		ldloc 9
		ldloc 10
		conv.i4
		ldc.i4 5
		mul
		ldloc 12
		conv.i4
		add
		ldc.i4 1
		conv.i4
		stelem.i4
		ldloc 12
		ldloc 12
		ldc.i4.1
		add
		stloc 12
		pop
		br L14
		L16:
		ldloc 10
		ldloc 10
		ldc.i4.1
		sub
		stloc 10
		call void [mscorlib]System.Console::WriteLine(int32)
		ldloc 0
		ldloc 0
		ldc.i4.1
		sub
		stloc 0
		pop
		br L17
		L19:
		ldloc 1
		ldc.i4 3
		mul
		ldloc 0
		ldc.i4 2
		mul
		add
		call void [mscorlib]System.Console::WriteLine(int32)
		ldc.i4 5
		newarr [mscorlib]System.Double
		stloc 5
		ldc.i4 5
		newarr [mscorlib]System.Int32
		stloc 2
		ldc.i4 0
		conv.i4
		stloc 13
		L22:
		ldloc 13
		conv.r8
		ldc.i4 5
		conv.r8
		blt L20
		ldc.i4.0
		br L21
		L20:
		ldc.i4.1
		L21:
		ldc.i4.1
		beq L23
		br L24
		L23:
		ldloc 5
		ldloc 13
		conv.i4
		call string [mscorlib]System.Console::ReadLine()
		call float64 [mscorlib]System.Double::Parse(string)
		conv.r8
		stelem.r8
		ldloc 2
		ldloc 13
		conv.i4
		call string [mscorlib]System.Console::ReadLine()
		call int32 [mscorlib]System.Int32::Parse(string)
		conv.i4
		stelem.i4
		ldloc 13
		ldloc 13
		ldc.i4.1
		add
		stloc 13
		pop
		br L22
		L24:
		ldc.i4 0
		conv.i4
		stloc 14
		L27:
		ldloc 14
		conv.r8
		ldc.i4 5
		conv.r8
		blt L25
		ldc.i4.0
		br L26
		L25:
		ldc.i4.1
		L26:
		ldc.i4.1
		beq L28
		br L29
		L28:
		ldloc 5
		.locals(int32 'indice1')
		ldloc 14
		conv.i4
		dup
		stloc 'indice1'
		ldelem.r8
		conv.r8
		ldc.r8 0.1
		conv.r8
		add
		call void [mscorlib]System.Console::WriteLine(float64)
		ldloc 2
		.locals(int32 'indice1')
		ldloc 14
		conv.i4
		dup
		stloc 'indice1'
		ldelem.i4
		conv.r8
		ldc.r8 0.1
		conv.r8
		add
		call void [mscorlib]System.Console::WriteLine(float64)
		ldloc 14
		ldloc 14
		ldc.i4.1
		add
		stloc 14
		pop
		br L27
		L29:
		ldc.i4 64
		newarr [mscorlib]System.Int32
		stloc 9
		ldc.i4 0
		conv.i4
		stloc 15
		L32:
		ldloc 15
		conv.r8
		ldc.i4 5
		conv.r8
		blt L30
		ldc.i4.0
		br L31
		L30:
		ldc.i4.1
		L31:
		ldc.i4.1
		beq L33
		br L34
		L33:
		ldloc 9
		ldloc 15
		conv.i4
		ldc.i4 8
		mul
		ldloc 15
		conv.i4
		add
		call string [mscorlib]System.Console::ReadLine()
		call bool [mscorlib]System.Boolean::Parse(string)
		conv.i4
		stelem.i4
		ldloc 9
		ldloc 15
		conv.i4
		ldc.i4 8
		mul
		ldloc 15
		conv.i4
		add
		ldloc 9
		.locals(int32 'indice1')
		ldloc 15
		conv.i4
		ldc.i4 8
		mul
		ldloc 15
		conv.i4
		add
		dup
		stloc 'indice1'
		ldelem.i4
		ldc.i4.1
		xor
		conv.i4
		stelem.i4
		ldloc 9
		.locals(int32 'indice1')
		ldloc 15
		conv.i4
		ldc.i4 8
		mul
		ldloc 15
		conv.i4
		add
		dup
		stloc 'indice1'
		ldelem.i4
		call void [mscorlib]System.Console::WriteLine(bool)
		ldloc 15
		ldloc 15
		ldc.i4.1
		add
		stloc 15
		pop
		br L32
		L34:
		ldc.i4 0
		conv.i4
		stloc 1
		ldc.i4 1
		conv.i4
		stloc 0
		ldloc 0
		ldloc 0
		ldc.i4.1
		add
		stloc 0
		conv.i4
		stloc 1
		ldloc 0
		call void [mscorlib]System.Console::WriteLine(int32)
		ldloc 1
		call void [mscorlib]System.Console::WriteLine(int32)
		ldc.i4 125
		newarr [mscorlib]System.Int32
		stloc 4
		ldloc 4
		ldc.i4 0
		conv.i4
		ldc.i4 5
		mul
		ldc.i4 0
		conv.i4
		add
		ldc.i4 5
		mul
		ldc.i4 0
		conv.i4
		add
		ldc.i4 1
		conv.i4
		stelem.i4
		ldloc 4
		.locals(int32 'indice1')
		ldc.i4 0
		conv.i4
		ldc.i4 5
		mul
		ldc.i4 0
		conv.i4
		add
		ldc.i4 5
		mul
		ldc.i4 0
		conv.i4
		add
		dup
		stloc 'indice1'
		ldelem.i4
		call void [mscorlib]System.Console::WriteLine(int32)
		ldc.i4 1
		call void [mscorlib]System.Console::WriteLine(bool)
		ldc.i4 1
		call void [mscorlib]System.Console::WriteLine(bool)
		ldc.i4 1
		call void [mscorlib]System.Console::WriteLine(bool)
		ldloc 4
		ldc.i4 0
		conv.i4
		ldc.i4 5
		mul
		ldc.i4 0
		conv.i4
		add
		ldc.i4 5
		mul
		ldc.i4 0
		conv.i4
		add
		ldloc 4
		.locals(int32 'indice1')
		ldc.i4 0
		conv.i4
		ldc.i4 5
		mul
		ldc.i4 0
		conv.i4
		add
		ldc.i4 5
		mul
		ldc.i4 0
		conv.i4
		add
		dup
		stloc 'indice1'
		ldelem.i4
		ldloc 4
		ldloc 'indice1'
		ldloc 4
		ldloc 'indice1'
		ldelem.i4
		ldc.i4.1
		add
		stelem.i4
		ldc.i4 0
		add
		conv.i4
		stelem.i4
		ldloc 4
		.locals(int32 'indice2')
		ldc.i4 0
		conv.i4
		ldc.i4 5
		mul
		ldc.i4 0
		conv.i4
		add
		ldc.i4 5
		mul
		ldc.i4 0
		conv.i4
		add
		dup
		stloc 'indice2'
		ldelem.i4
		call void [mscorlib]System.Console::WriteLine(int32)
		ldc.i4 1
		conv.i4
		stloc 0
		ldloc 0
		ldloc 0
		ldc.i4.1
		add
		stloc 0
		ldloc 0
		ldloc 0
		ldc.i4.1
		add
		stloc 0
		add
		conv.i4
		stloc 0
		ldloc 0
		call void [mscorlib]System.Console::WriteLine(int32)
		ldloc 1
		call void [mscorlib]System.Console::WriteLine(int32)
		ldloc 1
		ldloc 1
		ldc.i4.1
		sub
		stloc 1
		conv.i4
		stloc 0
		ldloc 0
		call void [mscorlib]System.Console::WriteLine(int32)
		ldloc 1
		call void [mscorlib]System.Console::WriteLine(int32)
		ldloc 4
		ldc.i4 0
		conv.i4
		ldc.i4 5
		mul
		ldc.i4 0
		conv.i4
		add
		ldc.i4 5
		mul
		ldc.i4 0
		conv.i4
		add
		ldc.i4 1
		conv.i4
		stelem.i4
		ldloc 4
		.locals(int32 'indice2')
		ldc.i4 0
		conv.i4
		ldc.i4 5
		mul
		ldc.i4 0
		conv.i4
		add
		ldc.i4 5
		mul
		ldc.i4 0
		conv.i4
		add
		dup
		stloc 'indice2'
		ldelem.i4
		ldloc 4
		ldloc 'indice2'
		ldloc 4
		ldloc 'indice2'
		ldelem.i4
		ldc.i4.1
		sub
		stelem.i4
		call void [mscorlib]System.Console::WriteLine(int32)
		ldloc 4
		.locals(int32 'indice3')
		ldc.i4 0
		conv.i4
		ldc.i4 5
		mul
		ldc.i4 0
		conv.i4
		add
		ldc.i4 5
		mul
		ldc.i4 0
		conv.i4
		add
		dup
		stloc 'indice3'
		ldelem.i4
		call void [mscorlib]System.Console::WriteLine(int32)
		ldloc 5
		.locals(int32 'indice3')
		ldc.i4 0
		conv.i4
		dup
		stloc 'indice3'
		ldelem.r8
		call void [mscorlib]System.Console::WriteLine(float64)
		ldloc 5
		.locals(int32 'indice3')
		ldc.i4 0
		conv.i4
		dup
		stloc 'indice3'
		ldelem.r8
		ldloc 5
		ldloc 'indice3'
		ldloc 5
		ldloc 'indice3'
		ldelem.r8
		ldc.i4.1
		conv.r8
		add
		stelem.r8
		call void [mscorlib]System.Console::WriteLine(float64)
		ldloc 5
		.locals(int32 'indice4')
		ldc.i4 0
		conv.i4
		dup
		stloc 'indice4'
		ldelem.r8
		ldloc 5
		ldloc 'indice4'
		ldloc 5
		ldloc 'indice4'
		ldelem.r8
		ldc.i4.1
		conv.r8
		sub
		stelem.r8
		call void [mscorlib]System.Console::WriteLine(float64)
		ldloc 5
		.locals(int32 'indice5')
		ldc.i4 0
		conv.i4
		dup
		stloc 'indice5'
		ldelem.r8
		call void [mscorlib]System.Console::WriteLine(float64)
		ldloc 0
		call void [mscorlib]System.Console::WriteLine(int32)
		ldloc 5
		ldc.i4 0
		conv.i4
		ldloc 0
		conv.r8
		stelem.r8
		ldloc 5
		.locals(int32 'indice5')
		ldc.i4 0
		conv.i4
		dup
		stloc 'indice5'
		ldelem.r8
		conv.r8
		ldc.r8 0.1
		conv.r8
		add
		call void [mscorlib]System.Console::WriteLine(float64)
		ldloc 5
		.locals(int32 'indice5')
		ldc.i4 0
		conv.i4
		dup
		stloc 'indice5'
		ldelem.r8
		conv.i4
		stloc 0
		ldloc 5
		.locals(int32 'indice5')
		ldc.i4 1
		conv.i4
		dup
		stloc 'indice5'
		ldelem.r8
		conv.r8
		ldloc 5
		.locals(int32 'indice5')
		ldloc 5
		.locals(int32 'indice5')
		ldc.i4 0
		conv.i4
		dup
		stloc 'indice5'
		ldelem.r8
		conv.i4
		dup
		stloc 'indice5'
		ldelem.r8
		conv.r8
		beq L35
		ldc.i4.0
		br L36
		L35:
		ldc.i4.1
		L36:
		ldc.i4.1
		bne.un L37
		ldc.i4 9999
		conv.r8
		ldloc 5
		.locals(int32 'indice5')
		ldc.i4 0
		conv.i4
		dup
		stloc 'indice5'
		ldelem.r8
		conv.r8
		sub
		call void [mscorlib]System.Console::WriteLine(float64)
		br L38
		L37:
		ldc.i4 9999
		neg
		call void [mscorlib]System.Console::WriteLine(int32)
		L38:
		ldloc 5
		ldc.i4 0
		conv.i4
		ldc.r8 6.11
		conv.r8
		stelem.r8
		ldloc 2
		ldc.i4 0
		conv.i4
		ldc.i4 4
		conv.i4
		stelem.i4
		ldloc 5
		.locals(int32 'indice5')
		ldc.i4 0
		conv.i4
		dup
		stloc 'indice5'
		ldelem.r8
		conv.r8
		ldloc 2
		.locals(int32 'indice5')
		ldc.i4 0
		conv.i4
		dup
		stloc 'indice5'
		ldelem.i4
		conv.r8
		mul
		conv.r8
		ldc.i4 4
		conv.r8
		div
		conv.r8
		stloc 16
		ldloc 16
		call void [mscorlib]System.Console::WriteLine(float64)
		ldloc 2
		.locals(int32 'indice5')
		ldc.i4 0
		conv.i4
		dup
		stloc 'indice5'
		ldelem.i4
		conv.r8
		ldloc 5
		.locals(int32 'indice5')
		ldc.i4 0
		conv.i4
		dup
		stloc 'indice5'
		ldelem.r8
		conv.r8
		mul
		conv.r8
		ldc.i4 6
		conv.r8
		mul
		conv.r8
		stloc 16
		ldloc 16
		call void [mscorlib]System.Console::WriteLine(float64)
		ldc.i4 5
		conv.r8
		ldc.r8 5.0
		conv.r8
		mul
		conv.r8
		ldc.i4 5
		conv.r8
		mul
		call void [mscorlib]System.Console::WriteLine(float64)
		ldc.i4 5
		conv.r8
		ldc.r8 5.0
		conv.r8
		add
		conv.r8
		ldc.i4 5
		conv.r8
		add
		call void [mscorlib]System.Console::WriteLine(float64)
		ldc.i4 5
		conv.r8
		ldc.r8 5.0
		conv.r8
		div
		conv.r8
		ldc.i4 5
		conv.r8
		div
		conv.r8
		ldc.i4 5
		conv.r8
		div
		call void [mscorlib]System.Console::WriteLine(float64)
		ldc.i4 5
		conv.r8
		ldc.r8 5.0
		conv.r8
		sub
		conv.r8
		ldc.i4 5
		conv.r8
		sub
		conv.r8
		ldc.i4 5
		conv.r8
		sub
		call void [mscorlib]System.Console::WriteLine(float64)
		ldc.i4 5
		conv.r8
		ldc.i4 5
		conv.r8
		beq L39
		ldc.i4.0
		br L40
		L39:
		ldc.i4.1
		L40:
		ldc.i4 1
		mul
		call void [mscorlib]System.Console::WriteLine(bool)
		ret
	}
}
