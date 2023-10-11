#version 330 core
in vec2 UV;
out vec4 outColor;

uniform sampler2D tex0;

void main() {
    outColor = vec4(texture(tex0, UV).rgb, 1);
//    outColor = vec4(1, 0, 0, 1);
}
//varying lowp vec2 UV;
//
//uniform sampler2D tex0;
//
//void main() {
//    gl_FragColor = vec4(texture2D(tex0, UV).rgb, 1);
//}