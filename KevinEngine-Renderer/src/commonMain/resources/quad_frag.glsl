//#version 300 es
varying lowp vec3 fragmentColor;
//out vec4 outColor;
void main() {
//    outColor = vec4(fragmentColor, 1);
    gl_FragColor = vec4(fragmentColor, 1);
}