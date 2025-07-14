import React, {useState} from 'react';
import {Button} from '@vaadin/react-components/Button.js';
import {HorizontalLayout} from '@vaadin/react-components/HorizontalLayout.js';

export default function Counter() {
    const [counter, setCounter] = useState(0);

    return (
        <HorizontalLayout theme="spacing" style={{ alignItems: 'baseline' }}>
            <Button onClick={() => setCounter(counter + 1)}>Button</Button>
            <p>Clicked {counter} times</p>
        </HorizontalLayout>
    );
}