document.addEventListener('DOMContentLoaded', function() {
    const abcInput = document.getElementById('abc-input');
    const abcOutput = document.getElementById('abc-output');
    const playButton = document.getElementById('play-button');
  
    const resizer = document.getElementById('resizer');
    const leftPane = document.getElementById('editor-pane');
    const rightPane = document.getElementById('render-pane');
  
    let isDragging = false;
    let isPlaying = false;
    let visualObj = null;
    let synth = new ABCJS.synth.CreateSynth();
    let audioContext = new AudioContext();
    let timingCallbacks = null;
    let clickedTime = null;
  
    resizer.addEventListener('mousedown', function (e) {
      isDragging = true;
      document.body.style.cursor = 'col-resize';
    });
  
    window.addEventListener('mousemove', function (e) {
      if (!isDragging) return;
  
      const container = document.getElementById('resizable-container');
      const containerRect = container.getBoundingClientRect();
      const offsetX = e.clientX - containerRect.left;
  
      const min = 200;
      const max = containerRect.width - 200;
      const leftWidth = Math.min(Math.max(offsetX, min), max);
  
      leftPane.style.width = `${leftWidth}px`;
      rightPane.style.width = `${containerRect.width - leftWidth - 8}px`; // 8px = resizer width
    });
  
    window.addEventListener('mouseup', function () {
      isDragging = false;
      document.body.style.cursor = 'default';
    });
  
    function renderAbc() {
      const abcCode = abcInput.value ? abcInput.value : abcInput.textContent;
      console.log(abcInput.textContent);
      visualObj = ABCJS.renderAbc("abc-output", abcCode, {
        responsive: "resize",
        add_classes: true
      })[0];
      console.log(visualObj);
  
      let visualSvg = document.querySelector("#abc-output > svg");
      console.log("visualSvg", visualSvg);
      visualSvg.addEventListener('click', (event) => {
        const clickedElement = event.target.closest('.abcjs-note');
        console.log("clickedElement", clickedElement)
        if (clickedElement) {
          const time = clickedElement.getAttribute('data-time');
          if (time) {
            clickedTime = parseFloat(time);
            console.log('Clicked note time:', clickedTime);
          } else {
            console.warn('Clicked note does not have data-time attribute.');
            clickedTime = null;
          }
        } else {
          clickedTime = null;
        }
      });
    }
  
    abcInput.addEventListener('input', renderAbc);
  
    async function startPlayback() {
      if (!visualObj || isPlaying) return;
  
      try {
        await synth.init({
          audioContext: audioContext,
          visualObj: visualObj,
          options: {
            soundFontUrl: "../../S2U/abcjs_sounds",
            pan: [-0.3, 0.3]
          }
        });
  
        await synth.prime();
  
        timingCallbacks = new ABCJS.TimingCallbacks(visualObj, {
          eventCallback: (event) => {
            // 이전 하이라이트 제거
            const lastHighlights = document.querySelectorAll(".abcjs-note_selected");
            lastHighlights.forEach(el => el.classList.remove("abcjs-note_selected"));
  
            // 현재 음표 하이라이트
            if (event && event.elements) {
              event.elements.forEach(group => {
                group.forEach(el => {
                  el.classList.add("abcjs-note_selected");
                });
              });
            }
          },
          beatCallback: null,
          lineEndCallback: null
        });
  
        timingCallbacks.start();
  
        synth.start();
        isPlaying = true;
        playButton.textContent = "Stop";
  
      } catch (error) {
        console.error("Error during playback:", error);
        isPlaying = false;
        playButton.textContent = "Play";
      }
    }
  
    function stopPlayback() {
      if (isPlaying) {
        if (synth) {
          synth.stop();
        }
        if (timingCallbacks) {
          timingCallbacks.pause();
        }
        const lastHighlights = document.querySelectorAll(".abcjs-note_selected");
        lastHighlights.forEach(el => el.classList.remove("abcjs-note_selected"));
        isPlaying = false;
        playButton.textContent = "Play";
      }
    }
  
    playButton.addEventListener('click', () => {
      if (isPlaying) {
        stopPlayback();
      } else {
        startPlayback();
      }
    });
  
    // Space 키를 눌렀을 때 재생/정지
    document.addEventListener('keydown', function(event) {
      if (event.code === 'Space') {
        if (isPlaying) {
          event.preventDefault(); // 스크롤 방지
          stopPlayback();
        } else if (visualObj) {
          // startPlayback();
        }
      }
    });
  
    // input 상자가 수정되면 노래 멈춤
    abcInput.addEventListener('input', () => {
      stopPlayback();
      renderAbc(); // 수정될 때마다 다시 렌더링
    });
  
    // 초기 렌더링
    renderAbc();

    window.renderAbc = renderAbc;
  });