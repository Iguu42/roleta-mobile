package com.example.roleta;

import static org.mockito.Mockito.verify;

import android.widget.ImageView;
import android.widget.TextView;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class PlayRouletteFragmentTest {

    @Mock
    TextView mockTitleTextView;

    @Mock
    ImageView mockEditButton;

    @Mock
    ImageView mockShareButton;

    @InjectMocks
    PlayRouletteFragment fragment;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        fragment.titleTextView = mockTitleTextView;

        fragment.shareButton = mockShareButton;
        fragment.editButton = mockEditButton;
    }

    @Test
    public void testSetTitle() {
        String expectedTitle = "Test Title";
        fragment.titleTextView.setText(expectedTitle);
        verify(mockTitleTextView).setText(expectedTitle);
    }

    @Test
    public void testShareButtonClicked() {
        fragment.shareButton.performClick();
        verify(mockShareButton).performClick();
    }

    @Test
    public void testEditButtonClicked() {
        fragment.editButton.performClick();
        verify(mockEditButton).performClick();
    }
}