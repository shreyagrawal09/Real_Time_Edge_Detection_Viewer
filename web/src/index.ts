<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Edge Detection Viewer</title>
    <link rel="stylesheet" href="../dist/styles.css">
</head>
<body>
    <div class="container">
        <h1>Edge Detection Viewer</h1>
                <div class="controls">
            <label>
                Select Filter:
                <select id="filterSelect">
                    <option value="edge">Edge Detection</option>
                    <option value="grayscale">Grayscale</option>
                </select>
            </label>
        </div>
        <div class="canvas-container">
            <canvas id="canvas"></canvas>
        </div>
        <div class="stats">
            <div id="stats-info">
                <p>Resolution: <span id="resolution">-</span></p>
                <p>FPS: <span id="fps">0</span></p>
                <p>Processing Time: <span id="processingTime">0</span>ms</p>
            </div>
        </div>
        <div class="controls">
            <button id="loadImageBtn">Load Sample Image</button>
            <button id="downloadBtn">Download Frame</button>
        </div>
    </div>
    <script src="../dist/index.js"></script>
</body>
</html>